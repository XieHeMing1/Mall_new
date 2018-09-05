package com.qy.zgz.mall.base

import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import com.qy.zgz.mall.vbar.VbarUtils
import com.qy.zgz.mall.widget.TisDialog
import com.zk.zk_online.base.BaseFragmentActivity
import org.xutils.common.Callback

/**
 * Created by LCB on 2018/3/29.
 */
abstract class BaseReadCardFragmentActivity : BaseFragmentActivity(){

    //检测微信登录接口线程
    var wx_handle= Handler()

    //微信临时授权二维码链接
    var wx_qrcode=""

    var isStartCountDown:Boolean=true

    //执行倒计时30秒
    var countDownTimer = object : CountDownTimer(20000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

            onTickView(millisUntilFinished)
        }

        override fun onFinish() {
            //清除会员登录信息
            SharePerferenceUtil.getInstance()
                    .setValue(Constance.member_Info,"")
            //清除商城会员登录accessToken
            SharePerferenceUtil.getInstance()
                    .setValue(Constance.user_accessToken,"")
            //清除商城会员登录shop_id
            SharePerferenceUtil.getInstance()
                    .setValue(Constance.shop_id,"")

            //显示是否登录
            isLogining()

            onTickViewFinish()
        }
    }


    override fun onResume() {
        //判断是否登录中
        isLogining()
        super.onResume()

    }
    //初始化登录中的布局及操作
    abstract fun initLoginView()

    //初始化未登录的布局及操作
    abstract fun initUnLoginView()

    //倒计时View操作
    abstract fun onTickView (millisUntilFinished: Long)

    //倒计时View完成操作
    abstract fun onTickViewFinish ()

    //显示登录二维码
    abstract fun showLoginQRcode (qrcode:String)

    //判断会员是否登录中
    fun isMemberLogining():Boolean{
        return !TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())
    }


    //判断是否登录中
    fun isLogining(){
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())){

            countDownTimer!!.cancel()

            //初始化未登录的布局及操作
            initUnLoginView()

            //开启登录识别扫描器
            startLoginRecognitionScan()
            //创建新的微信授权二维码
            CreateScanCode(SharePerferenceUtil.getInstance()
                    .getValue(Constance.MachineID,"").toString())
        }else{

            countDownTimer!!.cancel()

            //初始化登录中的布局及操作
            initLoginView()

            if (isStartCountDown){
                countDownTimer!!.start()
            }

        }
    }


    /**
     * 开启登录识别扫描器
     */
    fun startLoginRecognitionScan(){
        try {

        //开启扫描器识别
        VbarUtils.getInstance(this)
                .setScanResultExecListener(object: VbarUtils.ScanResultExecListener {
                    override fun scanResultExec(result: String) {
                        Log.i("scan_result",result+"  ")
                        if (!TextUtils.isEmpty(result)
                                && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())){
                            scanCardLogin(result)
                        }
                    }

                }).getScanResult()
        }catch (e:Exception){

        }
    }


    /**
     * 会员扫卡登录
     */
    fun scanCardLogin(scan_result:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("CardSN",scan_result)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        var dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetMemberInfoByCardNo,hashmap,object: XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
                var jsonResult= GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (jsonResult!!.has("return_Code") &&
                        jsonResult.get("return_Code").asString == "200" &&
                        jsonResult.get("Data").asJsonObject.get("Status").asString == "0"){
                    var data=jsonResult.getAsJsonObject("Data")

                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info,data.toString())

                    var Wid=data.get("WechatID").asString
                    var Bid=SharePerferenceUtil.getInstance().getValue(Constance.BranchID,"").toString()
                    var Vpn=SharePerferenceUtil.getInstance().getValue(Constance.Vpn,"").toString()

                    isLogining()

                    //登录商城
                    if (!TextUtils.isEmpty(Bid)
                            &&!TextUtils.isEmpty(Vpn)){
                        //执行商城会员登录
                        userLogin(Wid,Bid,Vpn)
                    }

                    //登录提示
                    TisDialog(this@BaseReadCardFragmentActivity).create()
                            .setMessage("登录成功!").show()


                }else{
                    CToast("登录失败")
                    startLoginRecognitionScan()
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
                startLoginRecognitionScan()
            }
        })

    }

    /**
     * 退出账号
     */
    fun exitMemberInfo(){
        //---退出登录---
        //清除会员登录信息
        SharePerferenceUtil.getInstance()
                .setValue(Constance.member_Info,"")
        //清除商城会员登录accessToken
        SharePerferenceUtil.getInstance()
                .setValue(Constance.user_accessToken,"")
        //清除商城会员登录shop_id
        SharePerferenceUtil.getInstance()
                .setValue(Constance.shop_id,"")

        //刷新个人信息界面
        isLogining()
    }

    /**
     * 会员登录接口
     */
    fun userLogin(wxopen_id :String,branch_id:String,vpn:String){
        var MacineId= SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString()
        var Bname= SharePerferenceUtil.getInstance().getValue(Constance.BranchName,"").toString()
        if (TextUtils.isEmpty(MacineId) || TextUtils.isEmpty(Bname)){
            return
        }
        var map=HashMap<String,String>()
        map.put("open_id",wxopen_id)
        map.put("branch_id",branch_id)
        map.put("child_url",vpn)
        map.put("deviceid",MacineId)
        map.put("branch_name",Bname)
        val memberInfo = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "")!!.toString(), MemberInfo::class.java)

        if (null != memberInfo) {
            map.put("cust_id", memberInfo.Id)
            map.put("mobile", memberInfo.Phone)
        } else {
            map.put("cust_id", "")
            map.put("mobile", "")
        }
        NetworkRequest.getInstance().userLogin(map,object : NetworkCallback<JsonObject>(){
            override fun onSuccess(data: JsonObject) {
                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken,data!!.get("accessToken").asString)
                SharePerferenceUtil.getInstance().setValue(Constance.shop_id,data!!.get("shop_id").asString)
            }

            override fun onFailure(code: Int, msg: String?) {
            }
        })
    }



    /**
     * 微信授权扫码登录
     */
    fun authorizedLogin(TmpGuid:String){
        if (!TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
            return
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("TempGuid",TmpGuid)
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetCustomerScanData,hashmap,object:XutilsCallback<String>(){

            override fun onSuccessData(result: String) {
                wx_qrcode=""
                var jsonResult=GsonUtil.jsonToObject(result,JsonObject::class.java)
                if (jsonResult!!.has("return_Code") &&
                        jsonResult.get("return_Code").asString == "200" &&
                        jsonResult.get("Data").asJsonObject.get("Status").asString == "0"){
                    var data=jsonResult.getAsJsonObject("Data")
                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info,data.toString())

                    var Wid=data.get("WechatID").asString
                    var Bid=SharePerferenceUtil.getInstance().getValue(Constance.BranchID,"").toString()
                    var Vpn=SharePerferenceUtil.getInstance().getValue(Constance.Vpn,"").toString()

                    //显示登录信息
                    isLogining()

                    //登录商城
                    if (!TextUtils.isEmpty(Bid)
                            &&!TextUtils.isEmpty(Vpn)){
                        //执行商城会员登录
                        userLogin(Wid,Bid,Vpn)
                    }

                    //登录提示
                    TisDialog(this@BaseReadCardFragmentActivity).create()
                            .setMessage("登录成功!").show()


                }else{
                    wx_handle.postDelayed(object :Runnable{
                        override fun run() {
                            authorizedLogin(TmpGuid)
                        }

                    },2500)

                }



            }

            override fun onFinished() {
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                wx_handle.postDelayed(object :Runnable{
                    override fun run() {
                        authorizedLogin(TmpGuid)
                    }

                },2500)
            }

        })
    }


    /**
     * 生成授权微信登陆二维码
     */
    fun CreateScanCode(MachineID:String){
        if (TextUtils.isEmpty(MachineID)){
            return
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("MachineID",MachineID);
        hashmap.put("MenuName","扫码登录");
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson( Constance.MEMBER_HOST+Constance.CreateScanCode,hashmap,object:XutilsCallback<String>()
        {
            override fun onSuccessData(result: String) {
                var jsonResult=GsonUtil.jsonToObject(result,JsonObject::class.java)
                if (jsonResult!!.has("return_Code") &&
                        jsonResult.get("return_Code").asString == "200"){

                    wx_qrcode=jsonResult!!.get("Data").asString
                    var TmpGuid=jsonResult!!.get("Data2").asString
                    //显示登录二维码
                    showLoginQRcode(wx_qrcode)

                    wx_handle.removeCallbacksAndMessages(null)
                    //开始循环接口，查看是否登录
                    wx_handle.post(object :Runnable{
                        override fun run() {
                            if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
                                authorizedLogin(TmpGuid)
                            }
                        }

                    })

                }
            }

            override fun onFinished() {
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
            }

        })
    }

    override fun onPause() {

        super.onPause()
        countDownTimer!!.cancel()
        reSetCountDownTimer()
        //停止扫描
        VbarUtils.getInstance(this).stopScan()
        wx_handle.removeCallbacksAndMessages(null)
    }

    override fun onStop() {

        super.onStop()
        countDownTimer!!.cancel()

    }



    override fun onDestroy() {

        countDownTimer!!.cancel()
        //停止扫描
        VbarUtils.getInstance(this).stopScan()
        wx_handle.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    //开启，关闭倒计时
    fun setSOrECountDown(isStart:Boolean){
        isStartCountDown=isStart
        if (isStartCountDown){
            countDownTimer!!.start()
        }else{
            countDownTimer!!.cancel()
        }
    }

    //用户操作
    override fun onUserInteraction() {
        super.onUserInteraction()
        Constance.lastTouchTime = System.currentTimeMillis()
        //重新倒计时
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "")!!.toString())) {
            countDownTimer!!.cancel()
        } else {
            countDownTimer!!.cancel()
            if (isStartCountDown){
                countDownTimer!!.start()
            }
        }


    }

    fun reSetCountDownTimer( millisInFuture:Long=20000, countDownInterval:Long=1000){
        countDownTimer=object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                onTickView(millisUntilFinished)
            }

            override fun onFinish() {
                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info,"")
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken,"")
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id,"")

                //显示是否登录
                isLogining()

                onTickViewFinish()
            }
        }
    }





}