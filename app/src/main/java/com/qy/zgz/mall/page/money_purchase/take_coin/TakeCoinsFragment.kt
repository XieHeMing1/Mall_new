package com.qy.zgz.mall.page.money_purchase.take_coin

import android.graphics.Rect
import android.os.Message
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.text.TextUtils
import android.view.View
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.Model.TakeCoins
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import com.qy.zgz.mall.widget.TisDialog
import com.qy.zgz.mall.widget.TisEditDialog
import com.zhy.autolayout.utils.AutoUtils
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.util.*

/**
 *提取游戏币
 */

@ContentView(R.layout.fragment_take_coins)
class TakeCoinsFragment : BaseFragment() {

    @ViewInject(R.id.rv_fg_take_coins_info)
    lateinit var rv_fg_take_coins_info:RecyclerView

    var takeCoinsAdapter:TakeCoinsAdapter?=null

    //提币数据,-1代表自定义币数,-2代表所有币
    var coinsValue:IntArray = intArrayOf(5,10,20,50,100,200,-1,-2)

    override fun init() {
        //配置显示的设置
        rv_fg_take_coins_info.layoutManager = GridLayoutManager(context, 2)
        rv_fg_take_coins_info.addItemDecoration(object:RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                outRect!!.set(AutoUtils.getPercentWidthSize(25), AutoUtils.getPercentHeightSize(25),
                        AutoUtils.getPercentWidthSize(25), AutoUtils.getPercentHeightSize(25))
            }
        })

        var initdata:ArrayList<TakeCoins>
        initdata=ArrayList<TakeCoins>()
        coinsValue.mapTo(initdata) { TakeCoins((it).toString()) }

        takeCoinsAdapter= TakeCoinsAdapter(context,initdata,object :TakeCoinsAdapter.AdapterItemClickListener{
            override fun itemClick(position: Int) {
                if(!kd.sp().isSuccessOutCoin) {
                    var dialog = TisDialog(context).create().setMessage("设备没币,请移步到其他机器!").show()
                    return
                }
                kd.sp().bdCoinOuted()
                kd.sp().bdCleanError()
                if (!(activity as BuyCoinsActivity).isMemberLogining()){
                    var dialog=TisDialog(activity).create().setMessage("未登录,请先登录!").show()

                }
                else if(!(activity as BuyCoinsActivity).isSuccessOpenSerial){
                    var dialog=TisDialog(activity).create().setMessage("设备故障,请联系管理员!").show()

                }
                else if (coinsValue[position]==-1){
                    var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)
                    if(memberInfo!!.Coins.toDouble().toInt()<=0){
                        var dialog=TisDialog(context).create().setMessage("游戏币不足").show()
                        return
                    }
                    var num_dialog= TisEditDialog(context).create().setMessage("请输入取币数目")
                            .setEditType(InputType.TYPE_CLASS_NUMBER).setNegativeButton {  }
                            .setPositiveButton { v,pwd->
                                var num=pwd
                                if(TextUtils.isEmpty(num)){
                                    var dialog=TisDialog(context).create().setMessage("请输入取币数目").show()
                                }
                                else if(num.toInt()>Constance.maxOutCoinValue){
                                    var dialog=TisDialog(context).create().setMessage("单次最多提币"+Constance.maxOutCoinValue).show()
                                }
                                else if(num.toInt()==0){
                                    var dialog=TisDialog(context).create().setMessage("提币数量不能为0")
                                            .setHandEventAfterDismiss {
                                                itemClick(position)
                                            }.show()
                                }
                                else if (memberInfo!!.IsScan.toBoolean()){
//                                    writeGetCoinNoPwd( UUID.randomUUID().toString(),num)
                                    var dialog= TisEditDialog(context).create().setMessage("请输入密码")
                                            .setNegativeButton {  }
                                            .setPositiveButton { v,pwd->
                                                writeGetCoin( UUID.randomUUID().toString(),num,pwd)
                                            }.show()
                                }
                                else{
                                    //密码验证
//                                var dialog= TisEditDialog(context).create().setMessage("请输入密码")
//                                        .setNegativeButton {  }
//                                        .setPositiveButton { v,pwd->
//                                            writeGetCoin( UUID.randomUUID().toString(),num,pwd)
//                                        }.show()

                                    //密码扫卡验证
                                    var dialog= TisEditDialog(context).create().setMessage("请输入密码")
                                            .setNegativeButton {  }
                                            .setScanCardListener { scan,pwd ->
                                                CheckScanCardLogin(scan,num.toString(),pwd)
                                            }.show()
                                }
                            }.show()

                }
                else{
                    var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)

                    var value = if(coinsValue[position]==-2){
                        memberInfo!!.Coins.toDouble().toInt()
                    }else{
                        coinsValue[position]
                    }

                    if(value>memberInfo!!.Coins.toDouble().toInt()||memberInfo!!.Coins.toDouble().toInt()<=0){
                        var dialog=TisDialog(context).create().setMessage("游戏币不足").show()
                    }
                    else if(value>Constance.maxOutCoinValue){
                        var dialog=TisDialog(context).create().setMessage("单次最多提币"+Constance.maxOutCoinValue).show()
                    }
                    else if (memberInfo!!.IsScan.toBoolean()){
//                        writeGetCoinNoPwd(UUID.randomUUID().toString()
//                                ,value.toString())
                        var dialog= TisEditDialog(context).create().setMessage("请输入密码")
                                .setNegativeButton {  }
                                .setPositiveButton { v,pwd->
                                    writeGetCoin(UUID.randomUUID().toString()
                                            ,value.toString(),pwd)
                                }.show()
                    }
                    else{
                        //密码验证
//                    var dialog= TisEditDialog(context).create().setMessage("请输入密码")
//                            .setNegativeButton {  }
//                            .setPositiveButton { v,pwd->
//                                writeGetCoin(UUID.randomUUID().toString()
//                                        ,value.toString(),pwd)
//                            }.show()

                        //密码扫卡验证
                        var dialog= TisEditDialog(context).create().setMessage("请输入密码")
                                .setNegativeButton {  }
                                .setScanCardListener { scan,pwd ->
                                    CheckScanCardLogin(scan,value.toString(),pwd)
                                }.show()
                    }

                }

            }
        })
        rv_fg_take_coins_info.adapter=takeCoinsAdapter


    }

    override fun onClick(v: View?) {
    }

    override fun ObjectMessage(msg: Message?) {
    }

    /**
     * 会员提币获取打印码
     */
    fun getBarCode(position: Int,pwd:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("PrintModelKey","1102")
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))


        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetBarCode,hashmap,object :XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {


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
     * 会员提币
     */
    fun writeGetCoin(barCode:String,qty:String,pwd:String){
        if(qty.toInt() <= 0){
            var dialog=TisDialog(context).create()
                    .setMessage("出币数不能为0!").show()
            return
        }
        if(TextUtils.isEmpty(pwd)){
            var dialog=TisDialog(context).create()
                    .setMessage("请输入密码!").show()
            return
        }
        var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)
        if(null==memberInfo){
            var dialog=TisDialog(context).create()
                    .setMessage("请先登录!").show()
            return
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("BarCode",barCode)
        hashmap.put("UserID",Constance.machineUserID)
        hashmap.put("ClassID",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString())
        hashmap.put("ClassTime",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString())
        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("CustID",memberInfo!!.Id)
//        hashmap.put("CustID","58dec5e5-2de5-48d6-95a4-35dc5d4b4537")
        hashmap.put("Qty",qty)
        hashmap.put("CustPassword",pwd)
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))

        var dia = KProgressHUD.create(activity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.WriteGetCoin,hashmap,object :XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    (context as BuyCoinsActivity).StockBillID=rjson!!.get("Data").asString
                    (context as BuyCoinsActivity).outCoins(qty.toInt())

                }else{
                    var dialog=TisDialog(context).create()
                            .setMessage(rjson!!.get("result_Msg").asString).show()

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
                var dialog=TisDialog(context).create()
                        .setMessage("网络错误！").show()

            }

        })
    }

    /**
     * 会员提币(没密码)
     */
    fun writeGetCoinNoPwd(barCode:String,qty:String){
        if(qty.toInt() <= 0){
            var dialog=TisDialog(context).create()
                    .setMessage("出币数不能为0!").show()
            return
        }

        var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)
        if(null==memberInfo){
            var dialog=TisDialog(context).create()
                    .setMessage("请先登录!").show()
            return
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("BarCode",barCode)
        hashmap.put("UserID",Constance.machineUserID)
        hashmap.put("ClassID",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString())
        hashmap.put("ClassTime",SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString())
        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("CustID",memberInfo!!.Id)
//        hashmap.put("CustID","58dec5e5-2de5-48d6-95a4-35dc5d4b4537")
        hashmap.put("Qty",qty)
        hashmap.put("IsScan",true.toString())

        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))

        var dia = KProgressHUD.create(activity).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.WriteGetCoin,hashmap,object :XutilsCallback<String>(){
            override fun onSuccessData(result: String?) {
                var rjson= GsonUtil.jsonToObject(result!!.toString(), JsonObject::class.java)
                if (rjson!!.has("return_Code")&&
                        rjson!!.get("return_Code").asString == "200") {

                    (context as BuyCoinsActivity).StockBillID=rjson!!.get("Data").asString
                    (context as BuyCoinsActivity).outCoins(qty.toInt())


                }else{
                    var dialog=TisDialog(context).create()
                            .setMessage(rjson!!.get("result_Msg").asString).show()

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
                var dialog=TisDialog(context).create()
                        .setMessage("网络错误！").show()

            }

        })
    }


    /**
     * 验证会员是否已登录
     */
    fun CheckScanCardLogin(scan_result:String,num:String,pwd:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("CardSN",scan_result)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        var dia = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetMemberInfoByCardNo,hashmap,object: XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
                var jsonResult= GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (jsonResult!!.has("return_Code") &&
                        jsonResult.get("return_Code").asString == "200" &&
                        jsonResult.get("Data").asJsonObject.get("Status").asString == "0"){
                    var data=jsonResult.getAsJsonObject("Data")

                    var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)

                    if (memberInfo!=null && memberInfo!!.Id==data.get("Id").asString){
                        writeGetCoin(UUID.randomUUID().toString(),num,pwd)
                    }else{
                        var dialog=TisDialog(context).create()
                                .setMessage("会员卡不一致!").show()
                    }



                }else{
                    var dialog=TisDialog(context).create()
                            .setMessage(jsonResult.get("result_Msg").asString).show()
                }

            }

            override fun onFinished() {
                if (dia!=null&&dia.isShowing){
                    dia.dismiss()
                }

            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                var dialog=TisDialog(context).create()
                        .setMessage("网络异常").show()

            }
        })

    }
}