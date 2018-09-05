package com.qy.zgz.mall.page.money_purchase.error_handle

import android.os.Message
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Dbsql.DBDao
import com.qy.zgz.mall.Dbsql.DBOutCoinRecord
import com.qy.zgz.mall.Dbsql.DBReceiveMoneyRecord
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd
import com.qy.zgz.mall.Model.BuyCoins
import com.qy.zgz.mall.Model.Exceptionhanding
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import com.qy.zgz.mall.widget.TisCashPayDialog
import com.qy.zgz.mall.widget.TisDialog
import com.qy.zgz.mall.widget.TisOutCoinsDialog
import kotlinx.android.synthetic.main.fragment_exception_handing.*
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 *  create by lcb
 *  异常处理界面
 */
@ContentView(R.layout.fragment_exception_handing)
class ExceptionHandingFragment : BaseFragment(),SerialPortListener {


    //是否成功打开串口
    var isSuccessOpenSerial:Boolean=false


    @ViewInject(R.id.btn_exception_handing_orderupdate)
    lateinit var btn_exception_handing_orderupdate : Button

    @ViewInject(R.id.btn_exception_handing_rehandle)
    lateinit var btn_exception_handing_rehandle : Button

    @ViewInject(R.id.rv_exception_handing_info)
    lateinit var rv_exception_handing_info : RecyclerView

    @ViewInject(R.id.iv_exception_handing_pre)
    lateinit var iv_exception_handing_pre : ImageView

    @ViewInject(R.id.iv_exception_handing_next)
    lateinit var iv_exception_handing_next : ImageView

    @ViewInject(R.id.btn_exception_handing_refund)
    lateinit var btn_exception_handing_refund : Button




    var exceptionHandingAdapter: ExceptionHandingAdapter?=null

    var outCoins_dialog: TisOutCoinsDialog?=null

    var StockBillID=""

    var CustID=""

    var CustName=""

    //每页多少行
    var row=5
    //当前页数
    var curPageNum=1
    //总页数
    var totalPageNum=1

    //接口总页数
    var orderPageNum=1

    //接口总条数%row的余数
    var orderRemainNum=0

    //本地现金异常数据条数
    var localCashItemNum=0

    var cashDialog: TisCashPayDialog?=null

    var lastCashBuyCoins=ArrayList<BuyCoins>() //（现金购买时）需要更新的套餐信息


    override fun init() {
        iv_exception_handing_pre.setOnClickListener(this)
        iv_exception_handing_next.setOnClickListener(this)
        btn_exception_handing_orderupdate.setOnClickListener(this)
        btn_exception_handing_rehandle.setOnClickListener(this)
        btn_exception_handing_refund.setOnClickListener(this)

        //设置布局管理器()
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_exception_handing_info.layoutManager = linearLayoutManager
        rv_exception_handing_info.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val typeid = SharePerferenceUtil.getInstance().getValue("typeId", "")!!.toString()
        //欢乐熊版本
        if (typeid == "25") {
            btn_exception_handing_rehandle.visibility=View.INVISIBLE
        }

    }

    override fun onResume() {
        //接受返回出币命令
        kd.sp().go(this)

        //本地现金记录数
        localCashItemNum=DBDao.getInstance().queryCashErrorBill()?.size?:0

        OrderAllUpdate()

        super.onResume()
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_exception_handing_rehandle->{
                kd.sp().bdCleanError()
                if (!isSuccessOpenSerial||!kd.sp().isSuccessOutCoin){
                    var dialog=TisDialog(context).create().setMessage("设备故障或没币，请先清除故障!").show()
                    return
                }
                exceptionHandingAdapter!!.list.forEach {
                    if(it.ischeck){
                        when(it.ErrType){
                            "CashErr"->{
                                if(!kd.sp().isOpenZBJ){
                                    var dialog=TisDialog(context).create().setMessage("纸币器异常").show()
                                    return
                                }

                                autoMathPackageList(it.Amount,it.CustomerID,it.CashErrorRecordId,it.CustomerName)

                            }
                            "MobileErr"->{
                                OrderUpdate(it.ID,it.POS,false,it.CustomerID,it.CustomerName)
                            }
                            "OutCoinErr"->{
                              getNoFinishCoins(it.ID,it.CustomerID,it.CustomerName)
                            }
                        }

                        return@forEach
                    }
                }
            }
            //退款
            R.id.btn_exception_handing_refund->{
                exceptionHandingAdapter!!.list.forEach {
                    if(it.ischeck){
                        when(it.ErrType){
                            "CashErr"->{
                                var dialog = TisDialog(context).create().setMessage("该订单不能退款").show()

                            }
                            "MobileErr"->{
                                OrderUpdate(it.ID,it.POS,true,it.CustomerID,it.CustomerName)
                            }
                            "OutCoinErr"->{
                                var dialog = TisDialog(context).create().setMessage("该订单不能退款").show()

                            }
                        }
                        return@forEach
                    }
                }

            }
            R.id.iv_exception_handing_next->{
                if(curPageNum>=totalPageNum){
                    return
                }
                curPageNum += 1
                getErrorOrderList()

            }

            R.id.iv_exception_handing_pre->{
                if (curPageNum<=1){
                    return
                }
                curPageNum -=1
                getErrorOrderList()
            }
            //订单同步
            R.id.btn_exception_handing_orderupdate->{
                OrderAllUpdate()
            }

        }

    }

    override fun ObjectMessage(msg: Message?) {
    }


    /**
     * 同步机器订单
     */
    fun OrderAllUpdate(){
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("正在同步订单,请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.OrderUpdate,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {


                }else{
                    var dialog=TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                //获取异常订单列表
                getErrorOrderList()
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }

            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                TisDialog(context)?.create()?.setMessage("网络故障或超时")?.show()
            }

        },30000)
    }

    /**
     * 获取机器移动订单同步
     */
    fun OrderUpdate(OrderID:String,OrderPos:String,isRefund:Boolean,CustomerID:String,CustomerName: String){
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("OrderID", OrderID)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.OrderUpdate,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    if(isRefund){
                        OrderRefund(OrderID)
                    }else{
                        orderReOperate(OrderPos,CustomerID,OrderID,CustomerName)

                    }


                }else{
                    var dialog = TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                var dialog = TisDialog(context).create().setMessage("网络故障").show()

            }

        })
    }
    /**
     * 获取机器获取异常记录
     */
    fun getAbnormityList(pageNum:Int){
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("SearchType", "0")
        hashmap.put("IsFinish", false.toString())
        hashmap.put("PageNum", pageNum.toString())
        hashmap.put("PageRows", row.toString())

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.GetAbnormityList,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    //显示数据
                    var showData=ArrayList<Exceptionhanding>()

                    var exceData=GsonUtil.jsonToList(rjson!!.getAsJsonArray("Data").toString(),Exceptionhanding::class.java) as ArrayList<Exceptionhanding>

                    orderPageNum=rjson!!.get("Data2").asInt/row
                    orderRemainNum=rjson!!.get("Data2").asInt%row
                    if (orderRemainNum!=0){
                        orderPageNum+=1
                    }

                    //判断是否有现金异常
                    if (orderPageNum<=curPageNum){
                        if (localCashItemNum>0){
                            if (orderPageNum==curPageNum){
                                //合拼本地数据和接口数据
                                if (null!=exceData) {
                                    showData.addAll(exceData)
                                    if (exceData.size < row) {
                                        showData.addAll(getLocalCashShowDataList(row - exceData.size, 0))

                                    }
                                }
                                else{
                                    showData.addAll(getLocalCashShowDataList(row, 0))

                                }
                            }else {
                                var offset=if (orderRemainNum==0){orderRemainNum }else{row-orderRemainNum}
                                var diffPage=if((curPageNum-1-orderPageNum)<0){0}else{(curPageNum-1-orderPageNum)}
                                getLocalCashShowDataList(row,offset+diffPage*row).let {
                                    if (null!=it){
                                        showData.addAll(it)
                                    }
                                }
                            }
                        }else{
                            if (null!=exceData){
                                showData.addAll(exceData)
                            }
                        }
                     }else {
                        if (null!=exceData){
                            showData.addAll(exceData)
                        }
                     }

                    exceptionHandingAdapter= ExceptionHandingAdapter(context,showData)
                    rv_exception_handing_info.adapter=exceptionHandingAdapter



                    var cashPageItem = if(orderRemainNum==0){
                       localCashItemNum
                    }else{
                        localCashItemNum-row+orderRemainNum
                    }

                    totalPageNum=orderPageNum+cashPageItem/row
                    if (cashPageItem%row!=0){
                        totalPageNum+=1
                    }


                    tv_exception_handing_pagenum.text=curPageNum.toString()+"/"+totalPageNum
                }else{

                    var dialog=TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

                var dialog=TisDialog(context).create().setMessage("网络异常!").show()

            }

        },30000)
    }


    /**
     * 移动订单退款
     */
    fun OrderRefund(order:String){
        if(null==exceptionHandingAdapter){
            return
        }
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("UserID", Constance.machineUserID)
        hashmap.put("OrderID", order)

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.OrderRefund,hashmap,object : XutilsCallback<String>() {
            override fun onSuccessData(result: String?) {
                var rjson = GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code") &&
                        rjson!!.get("return_Code").asString == "200") {


                    var dialog = TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                } else {

                    var dialog = TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                getErrorOrderList()
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

                var dialog = TisDialog(context).create().setMessage("网络异常!").show()

            }

        })
    }

    /**
     * 移动订单重新处理
     */
    fun orderReOperate(order:String,CustomerID:String,OrderID:String,CustomerName: String){
        if(null==exceptionHandingAdapter){
            return
        }
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("BarCode", UUID.randomUUID().toString())
        hashmap.put("UserID", Constance.machineUserID)
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("ClassTime", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString())
        hashmap.put("ClassID",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString())
        hashmap.put("OrderNO", order)

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.OrderReOperate,hashmap,object : XutilsCallback<String>() {
            override fun onSuccessData(result: String?) {
                var rjson = GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code") &&
                        rjson!!.get("return_Code").asString == "200") {

                    if (CustomerID == Constance.machineFLTUserID){
                        getNoFinishCoins(rjson!!.getAsJsonObject("Data").get("ReturnID").asString,CustomerID,CustomerName)
                    }else{
                        getErrorOrderList()
                       var dialog=TisDialog(context).create().setMessage("处理完成,币已存入卡中!").show()
                    }


                } else {

                    var dialog = TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

                var dialog = TisDialog(context).create().setMessage("网络异常!").show()

            }

        })
    }

    /**
     * 重新处理出币记录
     */
    fun getNoFinishCoins(StockBillID:String,CustID: String,CustName: String){
        if(null==exceptionHandingAdapter){
            return
        }
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()

        hashmap.put("StockBillID", StockBillID)

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.GetNoFinishCoins,hashmap,object : XutilsCallback<String>() {
            override fun onSuccessData(result: String?) {
                var rjson = GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code") &&
                        rjson!!.get("return_Code").asString == "200") {

                    outCoin(rjson!!.get("Data").asInt,StockBillID,CustID,CustName)

                } else {

                    var dialog = TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

                var dialog = TisDialog(context).create().setMessage("网络异常!").show()

            }

        })
    }



    /**
     * 大众会员更新出币记录
     */
    fun takeUpdateOutCoinLog(num:Int,BillID:String){

        var hashmap=HashMap<String,String>()
        hashmap.put("OutCoins",num.toString())
        hashmap.put("StockBillID",BillID)


        hashmap.put("CustID",Constance.machineFLTUserID)
        hashmap.put("IsSaveCard",false.toString())


        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.UpdateOutCoinLog,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    //修改本地出币数据状态
                    var sbillList:ArrayList<String> = ArrayList<String>()
                    sbillList.add(StockBillID)
                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList)


//                        var dialog=TisDialog(context).create().setMessage(rjson!!.get("result_Msg").asString).show()

                }else{

                  UpdateServerByLoalData()

//                    var dialog=TisDialog(context).create().setMessage( rjson!!.get("result_Msg").asString).show()

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                getErrorOrderList()
                StockBillID=""
                CustID=""
                CustName=""
                closeOutCoinDialog()
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                UpdateServerByLoalData()

            }

        })

    }


    /**
     * 出币
     */
    fun outCoin(num:Int,billid:String,cID: String,cName: String){
        StockBillID=billid
        CustID=cID
        CustName=cName
        outCoins_dialog = TisOutCoinsDialog(context).create().setTotalNum(num.toString() + "")
                .setNum("0").show()
        kd.sp().bdSendOutCoin(num,kd.sp().getDevice("3"),1)
    }


    /**
     * 关闭显示出币界面
     */
    fun closeOutCoinDialog(){
            if(null!=outCoins_dialog){

                outCoins_dialog!!.dismiss()

            }
    }


    //-------------------------出币处理---------------------------------------

    override fun onCoinOuting(count: Int) {
        saveLocalOutCoinRecord(count)
        if(null!=outCoins_dialog){
            outCoins_dialog!!.num = count.toString()
        }
    }

    override fun onCoinOutSuccess(count: Int) {
        saveLocalOutCoinRecord(count)
        takeUpdateOutCoinLog(count,StockBillID)

    }

    override fun onCoinOutFail(outCount: Int, count: Int, errorCode: String?) {
        kd.sp().bdCleanError()
        saveLocalOutCoinRecord(outCount)
        if (count>=0){
            var dialog=TisDialog(context).create().setMessage("没币或故障").show()
            takeUpdateOutCoinLog(outCount,StockBillID)

        }
    }

    override fun onReceivedMomey(amount: Int, macType: String) {
            if (cashDialog!=null&&cashDialog!!.isShowing){
                //初始化需要更新的套餐信息
                lastCashBuyCoins.clear()

                cashDialog!!.freshCountDown()

                //收钱指令
                kd.sp().sendGetMomeyCmd(macType)


            }else{
                //退钱指令
                kd.sp().sendOutMomeyCmd(macType)
            }
    }

    override fun onReceivedMomeySuccess(amount: Int, macType: String?) {
        if (cashDialog!=null&&cashDialog!!.isShowing){
            //存入本地数据
            var id=saveLocalCashRecord(cashDialog!!.hadPrice.toDouble()+amount,cashDialog!!.localId)
            cashDialog!!.localId = id

            autoMathPackageList((cashDialog!!.hadPrice.toDouble()+amount).toString(),CustID,cashDialog!!.localId,CustName)





        }
    }

    override fun onReceivedMomeyFail(macType: String?) {
        if (cashDialog!=null&&cashDialog!!.isShowing){
            cashDialog!!.freshCountDown()
        }
    }

    override fun onSendCompleteData(bytes: ByteArray?) {
    }

    override fun onSerialPortOpenFail(file: File?) {
    }

    override fun onSerialPortOpenSuccess(file: File?) {
    }

    override fun onMachieConnectedSuccess(device: String?) {
        //币斗
        if (device!!.contains(kd.sp().getDevice("3"))){
            isSuccessOpenSerial=true
            Log.e("MA","su")
        }

        //纸币机
        if (device!!.contains(kd.sp().getDevice("1"))||device!!.contains(kd.sp().getDevice("2"))){

        }
    }

    override fun onMachieCommectedFail(device: String?) {
        //币斗
        if (device!!.contains(kd.sp().getDevice("3"))){
            isSuccessOpenSerial=false
            Log.e("MA","FA")
        }
        Log.e("MAFA",device)
        //纸币机
        if (device!!.contains(kd.sp().getDevice("1"))||device!!.contains(kd.sp().getDevice("2"))){

        }

    }



    /**
     * 销售套餐(本地数据重新销售)
     */
    fun updateLocalSalePackage(outcoin:Int,isSave:Boolean,localId:Int,CustID:String,CustName:String,buyList:ArrayList<BuyCoins>,RemainAmount:Double) {

            val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("请稍后").show()
            val hashmap = java.util.HashMap<String, String>()

            hashmap.put("BarCode", UUID.randomUUID().toString())
            hashmap.put("UserID", Constance.machineUserID)
            hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "")!!.toString())

            hashmap.put("ClassTime", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime, "")!!.toString())
            hashmap.put("ClassID", SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID, "")!!.toString())

            val ha = java.util.HashMap<String, String>()
            buyList.forEach {
                ha.put(it.Id, it.BuyQty)
            }
            hashmap.put("PackageInfo", GsonUtil.objectToJson(ha))

            hashmap.put("IsScan", "0")
            hashmap.put("IsReOperate", "false")
            hashmap.put("IsSaveCard", isSave.toString() + "")
            hashmap.put("CustID", CustID)

            hashmap.put("PayType", "0")

            hashmap.put("PreferAmount", "0")

            hashmap.put("IsHandwork", false.toString() + "")

            hashmap.put("RemainAmount", String.format("%.2f",RemainAmount))

            hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
            HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.SalePackage, hashmap, object : XutilsCallback<String>() {
                override fun onSuccessData(result: String) {
                    val rjson = GsonUtil.jsonToObject(result, JsonObject::class.java)
                    if (rjson!!.has("return_Code") && rjson.get("return_Code").asString == "200") {

                        //修改本地现金数据状态
                        var id = java.util.ArrayList<Int>()
                        id.add(localId)
                        DBDao.getInstance().updateStateReceiveMoneyRecord(id)

                        if (isSave){
                            getErrorOrderList()
                            TisDialog(context).create().setMessage("币已成功存入卡中").show()
                        }else{
                            outCoin(outcoin,rjson.getAsJsonObject("Data").get("ReturnID").asString,CustID,CustName)
                        }

                    } else {
                         TisDialog(context).create().setMessage(rjson.get("result_Msg").asString).show()

                    }
                }

                override fun onError(ex: Throwable, isOnCallback: Boolean) {

                }

                override fun onCancelled(cex: Callback.CancelledException) {

                }

                override fun onFinished() {
                    if (dia != null && dia.isShowing) {
                        dia.dismiss()
                    }
                }


            })

    }

    /**
     * 自动匹配套餐
     */
    fun autoMathPackageList(price:String,CustID:String,localID:Int,CustName: String) {
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()

        val hashmap = HashMap<String, String>()

        hashmap.put("PackageType", "Pa01")
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "")!!.toString())

        hashmap.put("CustID", CustID)

        hashmap.put("Amount", price)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashmap, object : XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
                val rjson = GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code") && rjson.get("return_Code").toString() == "200"
                        && rjson.get("Data").asJsonArray.size()>0) {
                    if (cashDialog!=null&&cashDialog!!.isShowing){
                        cashDialog!!.dismiss()
                    }
                    var buylist=GsonUtil.jsonToList(rjson.get("Data").asJsonArray.toString(),BuyCoins::class.java) as ArrayList<BuyCoins>
                    var buyTotalPrice=0.00
                    var coinNum=0
                    buylist.forEach {
                        buyTotalPrice+=it.BuyAmount.toDouble()
                        coinNum+=(it.StandardCoins.toDouble().toInt()+it.Coins1.toDouble().toInt())*it.BuyQty.toDouble().toInt()
                    }
                    if (CustID==Constance.machineFLTUserID){
                        updateLocalSalePackage(coinNum,false,localID,CustID,CustName,buylist,price.toDouble()-buyTotalPrice)
                    }else{
                        updateLocalSalePackage(coinNum,true,localID,CustID,CustName,buylist,price.toDouble()-buyTotalPrice)
                    }

                } else {
                    if (cashDialog!=null&&cashDialog!!.isShowing){
                        cashDialog!!.hadPrice=price
                        TisDialog(context).create().setMessage("未匹配到套餐!").show()
                    }else{
                        showCashPayDialog(price,CustID,CustName,localID)
                    }

                }
            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {
                val dialog = TisDialog(context).create().setMessage("网络异常!").show()

            }

            override fun onCancelled(cex: Callback.CancelledException) {

            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }


        })
    }


    //获取本地现金异常数据
    fun getLocalCashShowDataList(limit:Int,offset:Int):ArrayList<Exceptionhanding>{
        var showData=ArrayList<Exceptionhanding>()
        var localData=DBDao.getInstance().queryCashErrorBillByNum(limit,offset)
        localData.forEach {
            var exce=Exceptionhanding()
            exce.Date=it.createtime
            exce.Amount=it.money.toString()
            exce.ErrType="CashErr"
            exce.CustomerID=it.custID
            exce.CustomerName=it.custName
            exce.CashErrorRecordId=it.id
            showData.add(exce)

        }
        return showData
    }

    //获取异常订单方法(调用异常订单接口)
    fun getErrorOrderList(){
        if (curPageNum>orderPageNum){
            getAbnormityList(orderPageNum)
        }else{
            getAbnormityList(curPageNum)
        }
    }


    /**
     * 存入本地数据库（出币数据）
     */
    fun saveLocalOutCoinRecord(count:Int){
        //存入本地数据库
        var outcointRecord: DBOutCoinRecord = DBOutCoinRecord()
        outcointRecord.outcount=count
        outcointRecord.isError=1
        outcointRecord.stockBillID=StockBillID
        outcointRecord.custID=CustID
        outcointRecord.custName=CustName
        outcointRecord.classId=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString()
        outcointRecord.classTime=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString()

        DBDao.getInstance().saveOutCoinsRecord(outcointRecord)
    }


    //本地出币记录更新数据库记录
    fun UpdateServerByLoalData(){
        var outListRecord= DBDao.getInstance().queryErrorBill()
        if (outListRecord==null||outListRecord.isEmpty()){
            return
        }
        var outList= java.util.ArrayList<HashMap<String, Any>>()
        outListRecord.forEach {
            var outHashmap=HashMap<String,Any>()
            outHashmap.put("StockBillID",it.stockBillID)
            outHashmap.put("OutCoins",it.outcount)
            outList.add(outHashmap)
        }

        var hashmap=HashMap<String,String>()
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "")!!.toString())

        hashmap.put("LocalData",GsonUtil.objectToJson(outList))



        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.UpdateServerByLocalData,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    //修改本地出币数据状态
                    var sbillList:ArrayList<String> =ArrayList<String>()
                    outList.forEach {
                        sbillList.add(it["StockBillID"].toString())
                    }

                    DBDao.getInstance().updateStateOutCoinsRecord(sbillList)


                }else{

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {

            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

            }

        })

    }

    /**
     * 存入本地数据库（现金数据）
     */
    fun saveLocalCashRecord(money:Double,localId: Int):Int{
        //存入本地数据库
        var moneyRecord: DBReceiveMoneyRecord = DBReceiveMoneyRecord()
        moneyRecord.id=localId
        moneyRecord.money=money
        moneyRecord.isError=1
        moneyRecord.classId=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString()
        moneyRecord.classTime=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString()
        moneyRecord.custID=CustID
        moneyRecord.custName=CustName


        return DBDao.getInstance().saveOrUpdateCashRecord(moneyRecord)
    }

    //显示纸币付款提示窗
    fun showCashPayDialog(hadPrice:String,cID: String,cName: String,localId: Int){
        CustID=cID
        CustName=cName
        cashDialog=TisCashPayDialog(context).create()
                .setLocalId(localId)
                .setHadPrice(hadPrice)
                .setIsMachTaoCan(false)
                .setUpdateErrorListListener {
                    getErrorOrderList()
                }
                .show()
    }
}