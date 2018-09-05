package com.qy.zgz.mall.page.money_purchase.error_handle

import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Dbsql.DBDao
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd
import com.qy.zgz.mall.Model.LiPay
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.*
import com.qy.zgz.mall.widget.TisDialog
import com.qy.zgz.mall.widget.TisOutCoinsDialog
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.io.File
import java.util.*

/**
 * create by lcb
 *
 * 交班
 */

@ContentView(R.layout.fragment_change_shifts)
class ChangeShiftsFragment : BaseFragment(), SerialPortListener {

    @ViewInject(R.id.tv_change_shifts_first_coin)
    lateinit var tv_change_shifts_first_coin:TextView

    @ViewInject(R.id.tv_change_shifts_balance_coin)
    lateinit var tv_change_shifts_balance_coin:TextView

    @ViewInject(R.id.tv_change_shifts_sale_amount)
    lateinit var tv_change_shifts_sale_amount:TextView

    @ViewInject(R.id.tv_change_shifts_amount_diff)
    lateinit var tv_change_shifts_amount_diff:TextView

    @ViewInject(R.id.tv_change_shifts_wx_amount)
    lateinit var tv_change_shifts_wx_amount:TextView

    @ViewInject(R.id.tv_change_shifts_check_coin)
    lateinit var tv_change_shifts_check_coin:TextView

    @ViewInject(R.id.tv_change_shifts_add_coin)
    lateinit var tv_change_shifts_add_coin:TextView

    @ViewInject(R.id.tv_change_shifts_out_coin)
    lateinit var tv_change_shifts_out_coin:TextView

    @ViewInject(R.id.tv_change_shifts_coin_diff)
    lateinit var tv_change_shifts_coin_diff:TextView

    @ViewInject(R.id.tv_change_shifts_fast_cash)
    lateinit var tv_change_shifts_fast_cash:TextView

    @ViewInject(R.id.tv_change_shifts_fast_deposit)
    lateinit var tv_change_shifts_fast_deposit:TextView

    @ViewInject(R.id.tv_change_shifts_zfb_amount)
    lateinit var tv_change_shifts_zfb_amount:TextView

    @ViewInject(R.id.btn_change_shifts_clear_coins)
    lateinit var btn_change_shifts_clear_coins:Button


    @ViewInject(R.id.btn_change_shifts_jb)
    lateinit var btn_change_shifts_jb:Button

    @ViewInject(R.id.tv_change_shifts_state)
    lateinit var tv_change_shifts_state:TextView

    @ViewInject(R.id.tv_change_shifts_classtime)
    lateinit var tv_change_shifts_classtime:TextView

    @ViewInject(R.id.tv_change_shifts_jbtime)
    lateinit var tv_change_shifts_jbtime:TextView

    var outCoins_dialog:TisOutCoinsDialog?=null

    //是否成功打开串口
    var isSuccessOpenSerial:Boolean=false


    override fun init() {
        btn_change_shifts_clear_coins.setOnClickListener(this)
        btn_change_shifts_jb.setOnClickListener(this)

        btn_change_shifts_clear_coins.isSelected=true

        val typeid = SharePerferenceUtil.getInstance().getValue("typeId", "")!!.toString()
        //欢乐熊版本
        if (typeid == "25") {
            btn_change_shifts_clear_coins.visibility=View.GONE
        }


        //获取机器信息
        getDeviceInfo(SharePerferenceUtil.getInstance().getValue(Constance.mac_Address,"").toString())

    }

    override fun onResume() {
        kd.sp().go(this)
        super.onResume()
    }


    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_change_shifts_clear_coins->{


                if (btn_change_shifts_clear_coins.isSelected){
                    btn_change_shifts_clear_coins.isSelected=false
                    btn_change_shifts_clear_coins.text="暂停"
                    btn_change_shifts_jb.setTextColor(resources.getColor(R.color.color_999999))
                    btn_change_shifts_jb.isEnabled=false
                    //清币
                    kd.sp().bdCleanError()
                    kd.sp().outAllCoin()


                }else{
                    btn_change_shifts_clear_coins.isSelected=true
                    btn_change_shifts_clear_coins.text="清币"
                    machineChangeCleanCoin(tv_change_shifts_check_coin.text.toString(),false.toString())
                    btn_change_shifts_jb.setTextColor(resources.getColor(R.color.color_blue))
                    btn_change_shifts_jb.isEnabled=true
                    //结束清币
                    kd.sp().bdCoinOuted()
                    kd.sp().bdCleanError()

                }

            }
            R.id.btn_change_shifts_jb->{
                machineChange()
            }
        }
    }

    override fun ObjectMessage(msg: Message?) {
    }


    /**
     * 获取机器信息
     */
    fun getDeviceInfo(mac:String){
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>();
        hashmap.put("MAC",mac)
        hashmap.put("MachineTypeID","1")
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.MacGetMachineClassInfo,hashmap,object:XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result,JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200"){

                    //保存机器信息
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassTime, rjson!!.getAsJsonObject("Data").get("ClassTime").asString)
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassID, rjson!!.getAsJsonObject("Data").get("ClassID").asString)

                    OrderUpdate()
                }else{
                    var dialog=TisDialog(context).create().setMessage("获取机器信息失败,"+rjson!!.get("result_Msg").asString+",请刷新!").show()

                }
            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                var dialog=TisDialog(context).create().setMessage("获取机器信息失败，请刷新!").show()

            }
        })

    }


    /**
     * 获取机器移动订单同步
     */
    fun OrderUpdate(){
        var hashmap= HashMap<String,String>()
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.OrderUpdate,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    tv_change_shifts_state.text=""


                }else{
                    tv_change_shifts_state.text="订单同步失败,"+rjson!!.get("result_Msg")+"，请重新同步!"
                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                getMachineChangeInfo()
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                tv_change_shifts_state.text="订单同步失败，请重新同步!"
            }

        },30000)
    }

    /**
     * 获取机器交班信息
     */
    fun getMachineChangeInfo(){
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("UserID",Constance.machineUserID)
        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("ClassTime",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString())
        hashmap.put("ClassID",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString())
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.GetHand2Check,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                   var data= rjson!!.getAsJsonObject("Data")
                    tv_change_shifts_first_coin.text=data.getAsJsonObject("HandInfo").get("InitToken").asString
                    tv_change_shifts_out_coin.text=data.getAsJsonObject("HandInfo").get("OutToken").asString
                    tv_change_shifts_balance_coin.text=data.getAsJsonObject("HandInfo").get("BalanceToken").asString
                    tv_change_shifts_add_coin.text=data.getAsJsonObject("HandInfo").get("AddToken").asString
                    tv_change_shifts_coin_diff.text="0"
                    tv_change_shifts_sale_amount.text="0"

                    //获取本地现金数据
                    tv_change_shifts_fast_cash.text="0"
                    var fastCash=DBDao.getInstance().queryCashBillByClass()
                    if (null!=fastCash&&fastCash.isNotEmpty()){
                        var fCash=0.00
                        fastCash.forEach {
                            fCash+=it.money
                        }
                        tv_change_shifts_fast_cash.text=fCash.toString()
                    }

                    tv_change_shifts_amount_diff.text="0"
                    var classTime=data.getAsJsonObject("HandInfo").get("ClassTime").asString
                    tv_change_shifts_classtime.text=classTime.substring(0,classTime.indexOf("T"))+SharePerferenceUtil.getInstance().getValue(Constance.MachineClassNAME,"").toString()
                    tv_change_shifts_jbtime.text=data.getAsJsonObject("HandInfo").get("LastHandDate").asString.replace("T"," ")+"～"+data.getAsJsonObject("HandInfo").get("HandDate").asString.replace("T"," ")

                    tv_change_shifts_check_coin.text=(context as ErrorHandleActivity).ClearNum.toString()

                    var handdata=data.getAsJsonArray("HandEntryInfo")
                    handdata.forEach {
                        when(it.asJsonObject.get("PaymentType").asString){
                            "1"->{
                                tv_change_shifts_fast_deposit.text=it.asJsonObject.get("ReceivableAmount").asString
                            }
                            "6"->{
                                tv_change_shifts_wx_amount.text=it.asJsonObject.get("ReceivableAmount").asString
                            }
                            "7"->{
                                tv_change_shifts_zfb_amount.text=it.asJsonObject.get("ReceivableAmount").asString
                            }else->{

                            }
                        }
                    }
                    tv_change_shifts_fast_deposit.text="0"
                }else{

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

            }

        })
    }

    /**
     * 机器交班清币
     */
    fun machineChangeCleanCoin(Tokens:String,IsFinish:String){
        var memberInfo: MemberInfo? = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java) ?: return

        var hashmap= HashMap<String,String>()
        var ClearID=(context as ErrorHandleActivity).ClearID

        if (!TextUtils.isEmpty(ClearID)){
            hashmap.put("ClearID",ClearID)
        }

        hashmap.put("UserID",Constance.machineUserID)

        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        var ct=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString()
        hashmap.put("ClassTime",ct)
        hashmap.put("ClassID",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString())
        hashmap.put("Tokens",Tokens)
        hashmap.put("IsFinish",IsFinish)
        hashmap.put("CustID",memberInfo!!.Id)

        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.WriteClearCoin,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    (context as ErrorHandleActivity).ClearID=rjson!!.get("Data").asString

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
     * 机器交班
     */
    fun machineChange(){
        //防止1秒内多次点击
        if (Utils.isFastClick(1000)||TextUtils.isEmpty(tv_change_shifts_classtime.text.toString())){
            return
        }
        val dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("BarCode",UUID.randomUUID().toString())
        hashmap.put("UserID",Constance.machineUserID)

        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        var ct=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString()
        hashmap.put("ClassTime",ct)
        hashmap.put("ClassID",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString())

        var ClearID=(context as ErrorHandleActivity).ClearID

        if (!TextUtils.isEmpty(ClearID)){
            hashmap.put("IsClear",true.toString())
            hashmap.put("CheckTokenQty",tv_change_shifts_check_coin.text.toString())
        }else{
            hashmap.put("IsClear",false.toString())
            hashmap.put("CheckTokenQty",tv_change_shifts_balance_coin.text.toString())
        }



        var liPay=ArrayList<LiPay>()
        var l= LiPay("0",tv_change_shifts_fast_cash.text.toString(),"")
        liPay.add(l)
        hashmap.put("LiPayType",GsonUtil.objectToJson(liPay))


        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.Hand2Check,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {
                    //清除清币记录

                    //修改班次信息
                    var data2=rjson.getAsJsonObject("Data2")

                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassTime,data2!!.get("StartTime").asString)
                    SharePerferenceUtil.getInstance().setValue(Constance.MachineClassID, data2!!.get("Id").asString)

                    getMachineChangeInfo()

                    var dialog=TisDialog(context).create().setMessage("交班成功").show()
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
                var dialog=TisDialog(context).create().setMessage("交班失败").show()

            }

        })
    }


    //--------------------------出币处理--------------------------------------

    /**
    * 显示出币界面
    */
    fun outCoins(){
        kd.sp().outAllCoin()
    }

    /**
     * 关闭显示出币界面
     */
    fun closeOutCoinDialog(){
        handler.postDelayed(Runnable {
            if(null!=outCoins_dialog){

                outCoins_dialog!!.dismiss()

            }
        },2000)
    }

    override fun onCoinOuting(count: Int) {

        tv_change_shifts_check_coin.text=(1+ tv_change_shifts_check_coin.text.toString().toInt()).toString()
        tv_change_shifts_coin_diff.text=(tv_change_shifts_check_coin.text.toString().toInt()-tv_change_shifts_balance_coin.text.toString().toInt()).toString()


    }

    override fun onCoinOutSuccess(count: Int) {
        btn_change_shifts_jb.setTextColor(resources.getColor(R.color.color_blue))
        btn_change_shifts_jb.isEnabled=true
        kd.sp().bdCleanError()
        btn_change_shifts_clear_coins.isSelected=true
        btn_change_shifts_clear_coins.text="清币"
        (context as ErrorHandleActivity).ClearNum=tv_change_shifts_check_coin.text.toString()
        machineChangeCleanCoin(tv_change_shifts_check_coin.text.toString(),true.toString())
//        var dialog=TisDialog(context).create().setMessage("清币完成").show()


    }

    override fun onCoinOutFail(outCount: Int, count: Int, errorCode: String?) {
        if (count>=0) {
            btn_change_shifts_jb.setTextColor(resources.getColor(R.color.color_blue))
            btn_change_shifts_jb.isEnabled = true
            kd.sp().bdCleanError()
            btn_change_shifts_clear_coins.isSelected = true
            btn_change_shifts_clear_coins.text = "清币"


            (context as ErrorHandleActivity).ClearNum = tv_change_shifts_check_coin.text.toString()

            machineChangeCleanCoin(tv_change_shifts_check_coin.text.toString(), true.toString())

        }
    }

    override fun onReceivedMomey(amount: Int, macType: String?) {
    }

    override fun onReceivedMomeySuccess(amount: Int, macType: String?) {
    }

    override fun onReceivedMomeyFail(macType: String?) {
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
    }

    override fun onMachieCommectedFail(device: String?) {
        //币斗
        if (device!!.contains(kd.sp().getDevice("3"))){
            isSuccessOpenSerial=false
            Log.e("MA","FA")
        }

    }
}