package com.qy.zgz.mall.page.index_function

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Dbsql.DBDao
import com.qy.zgz.mall.Model.Cranemaapi
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.Model.Version
import com.qy.zgz.mall.MyApplication
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseActivity
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.NetworkCallback
import com.qy.zgz.mall.network.NetworkRequest
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity
import com.qy.zgz.mall.slot_machines.game.SlotMachinesActivity
import com.qy.zgz.mall.utils.*
import com.qy.zgz.mall.vbar.VbarUtils
import com.qy.zgz.mall.widget.TisDialog
import kotlinx.android.synthetic.main.activity_index_funciton.*
import org.xutils.common.Callback
import java.io.File
import java.lang.Exception


class IndexFuncitonActivity : BaseActivity() {

    companion object {
        //是否跳转到会员中心标识
        var isGoMemberCenter:String="isGoMemberCenter"
    }

    private var cinemaType = "1"
    private var cinemaId = ""
    private var shop_name = ""
    var wx_handle=Handler()


    //当前视频序号
    private var videoIndex = 0
    //视频数
    private var videoSize = 0

    //获取机器信息接口循环次数
    private var mtime=0

    //执行倒计时30秒
    var countDownTimer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

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
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        cinemaType = SharePerferenceUtil.getInstance().getValue("typeId", "") as String

        setContentView(R.layout.activity_index_funciton)

//        if (cinemaType=="7"){
//            setContentView(R.layout.activity_index_funciton_thc)
//        }else {
//            setContentView(R.layout.activity_index_funciton)
//        }

        MyApplication.getInstance().addActivity(this)



        cinemaId = SharePerferenceUtil.getInstance().getValue("cinemaid", "") as String
        shop_name= SharePerferenceUtil.getInstance().getValue("type_shop_name", "") as String

        //清除会员登录信息
        SharePerferenceUtil.getInstance()
                .setValue(Constance.member_Info,"")
        //清除商城会员登录accessToken
        SharePerferenceUtil.getInstance()
                .setValue(Constance.user_accessToken,"")
        //清除商城会员登录shop_id
        SharePerferenceUtil.getInstance()
                .setValue(Constance.shop_id,"")
//        //清除机器场地BranchID
//        SharePerferenceUtil.getInstance()
//                .setValue(Constance.BranchID,"")
//        //清除机器ID
//        SharePerferenceUtil.getInstance()
//                .setValue(Constance.MachineID,"")
//        //清除机器VPN
//        SharePerferenceUtil.getInstance()
//                .setValue(Constance.Vpn,"")

//        //清除清币记录
//        SharePerferenceUtil.getInstance()
//                .setValue(Constance.MachineClearID,"")
//        SharePerferenceUtil.getInstance()
//                .setValue(Constance.MachineClearNum,"")



        tv_index_function_version.text="版本:"+DeviceUtil.getVersionName(this)
        tv_index_function_shopname.text="店铺:"+shop_name

//        Glide.with(this).load(R.drawable.bg_zzpact).asGif()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(sdv_index_function_zzpgif)

//        if (cinemaType=="7"){
//            //天河城店铺
//            iv_index_function_qrcode.visibility=View.GONE
//            iv_index_function_bottom.setImageResource(R.drawable.index_buy_coins)
//        }else{
//            iv_index_function_qrcode.visibility=View.VISIBLE
//            iv_index_function_bottom.setImageResource(R.drawable.bg_index_thc)
//        }

        tv_index_function_shopname.setOnLongClickListener {
            v ->
            UnityDialog(this).setHint("是否更换店铺？")
                    .setCancel("取消",null)
                    .setConfirm("确定",object:UnityDialog.OnConfirmDialogListener{
                        override fun confirm(unityDialog: UnityDialog?, content: String?) {
                            SharePerferenceUtil.getInstance().setValue("typeId", "")
                            SharePerferenceUtil.getInstance().setValue("cinemaid", "")
                            SharePerferenceUtil.getInstance().setValue("type_shop_name", "")
                            //清除机器场地BranchID
                            SharePerferenceUtil.getInstance()
                                    .setValue(Constance.BranchID,"")
                            //清除机器ID
                            SharePerferenceUtil.getInstance()
                                    .setValue(Constance.MachineID,"")
                            //清除机器VPN
                            SharePerferenceUtil.getInstance()
                                    .setValue(Constance.Vpn,"")

                            //清除会员登录信息
                            SharePerferenceUtil.getInstance()
                                    .setValue(Constance.member_Info,"")
                            //清除商城会员登录accessToken
                            SharePerferenceUtil.getInstance()
                                    .setValue(Constance.user_accessToken,"")

                            //清除商城会员登录shop_id
                            SharePerferenceUtil.getInstance()
                                    .setValue(Constance.shop_id,"")

                            unityDialog!!.dismiss()
                            MyApplication.getInstance().restartApp()

                        }

                    })
            false
        }

        //获取机器信息
        getDeviceInfo(SharePerferenceUtil.getInstance().getValue(Constance.mac_Address,"").toString())

        updateVersion()

        //轮播视频
        itemVideo()
        //轮播图及商品信息
        getCranema()

        //清理本地数据
        DBDao.getInstance().cleanNormalData()
    }

    override fun onResume() {

        //        播放视频
        playVideo()
        //是否隐藏登录
        isLogining()
        handler.removeCallbacks(noTouchRunnable)
        handler.postDelayed(noTouchRunnable,40*1000)
        super.onResume()
    }


    override fun ObjectMessage(msg: Bundle?) {
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //兑换商城
            R.id.btn_index_function_shopping->{
                startActivity(Intent(mContext, MallActivity::class.java))
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)
            }
            //购币取币
            R.id.btn_index_function_purchase->{
                startActivity(Intent(mContext,BuyCoinsActivity::class.java))
            }
            //购币取币(底部图片)
            R.id.iv_index_function_bottom->{
                startActivity(Intent(mContext,BuyCoinsActivity::class.java))
            }
            //会员中心未登录
            R.id.tv_index_function_member_center->{
                var intent=Intent(this, MallActivity::class.java)
                intent.putExtra(isGoMemberCenter,true)
                startActivity(intent)
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

            }
            //会员中心已登录
            R.id.arl_index_function_member_center->{
                var intent=Intent(this, MallActivity::class.java)
                intent.putExtra(isGoMemberCenter,true)
                startActivity(intent)
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)
            }
        //转转盘
            R.id.arl_game_zzp->{
                if(cinemaType=="20"){
                    return
                }
                    var intent=Intent(this,SlotMachinesActivity::class.java)
                    startActivity(intent)
                    // 定义出入场动画
                    overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)
            }

        //客服中心
            R.id.arl_customer_serivce->{
                var dialog=CustomerServiceDialog(this).create().show()
            }
            //退出登录
            R.id.btn_index_function_exitlogin->{
                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info,"")
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken,"")
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id,"")

                isLogining()

            }
        }
    }


    /**
     * 会员扫卡登录
     */
    fun scanCardLogin(scan_result:String){
        var hashmap=HashMap<String,String>()
        hashmap.put("CardSN",scan_result)
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        var dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetMemberInfoByCardNo,hashmap,object: XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
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
                    tv_index_function_username.text="会员中心 ! "+data.get("CustName")?.asString
                    isLogining()

                    //登录商城
                    if (!TextUtils.isEmpty(Bid)
                            &&!TextUtils.isEmpty(Vpn)){
                        //执行商城会员登录
                        userLogin(Wid,Bid,Vpn)
                    }

                    //登录提示
                    TisDialog(mContext).create()
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
     * 开启登录识别扫描器
     */
    fun startLoginRecognitionScan(){
        try {

        //开启扫描器识别
        VbarUtils.getInstance(this)
                .setScanResultExecListener(object:VbarUtils.ScanResultExecListener{
                    override fun scanResultExec(result: String) {
                        if (!TextUtils.isEmpty(result)
                                 && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
                            scanCardLogin(result)
                        }
                    }

                }).getScanResult()

    }catch (e:Exception){

    }
    }


    /**
     * 获取机器信息
     */
    fun getDeviceInfo(mac:String){
        var hashmap= HashMap<String,String>();
        hashmap.put("MAC",mac)
        hashmap.put("MachineTypeID","1")
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.MacGetMachineClassInfo,hashmap,object:XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result,JsonObject::class.java)
              if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200"){

                  var MachineID= rjson!!.getAsJsonObject("Data").get("MachineID").asString
                  //保存机器ID
                  SharePerferenceUtil.getInstance().setValue(Constance.MachineID, MachineID)
                  SharePerferenceUtil.getInstance().setValue(Constance.MachineClassTime, rjson!!.getAsJsonObject("Data").get("ClassTime").asString)
                  SharePerferenceUtil.getInstance().setValue(Constance.MachineClassID, rjson!!.getAsJsonObject("Data").get("ClassID").asString)

                  try {
                      var datas=rjson!!.getAsJsonObject("Data2")
                      if (datas!!.has("BranchID")) {
                              SharePerferenceUtil.getInstance().setValue(Constance.BranchID, datas.get("BranchID").asString)
                      }
                      if (datas!!.has("Vpn")) {
                              SharePerferenceUtil.getInstance().setValue(Constance.Vpn, datas.get("Vpn").asString)
                      }
                      if (datas!!.has("Name")) {
                              SharePerferenceUtil.getInstance().setValue(Constance.BranchName, datas.get("Name").asString)
                      }
                    }catch (e:Exception){

                    }

                  //更改机器状态
                  getMachineLogout(MachineID)

                  //创建授权二维码
//                  CreateScanCode(MachineID)
              }
            }

            override fun onFinished() {

            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                mtime++;
                if(mtime<=10){
                    handler.postDelayed(Runnable { getDeviceInfo(mac) },6000)
                }

            }
        })

    }

    /**
     * 会员登录接口
     */
    fun userLogin(wxopen_id :String,branch_id:String,vpn:String){
        var MacineId=SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString()
        var Bname=SharePerferenceUtil.getInstance().getValue(Constance.BranchName,"").toString()
        if (TextUtils.isEmpty(MacineId)||TextUtils.isEmpty(Bname)){
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
        if (!TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
            return;
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("TempGuid",TmpGuid)
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))

        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetCustomerScanData,hashmap,object:XutilsCallback<String>(){

            override fun onSuccessData(result: String) {
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


                    isLogining()

                    //登录商城
                    if (!TextUtils.isEmpty(Bid)
                           &&!TextUtils.isEmpty(Vpn)){
                        //执行商城会员登录
                        userLogin(Wid,Bid,Vpn)
                    }

                    //登录提示
                    TisDialog(mContext).create()
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
        Log.e("MachineID",MachineID)
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

                    var qrcode=jsonResult!!.get("Data").asString
                    var TmpGuid=jsonResult!!.get("Data2").asString

                    iv_index_function_qrcode.setImageBitmap(QRBitmapUtils.createQRCode(qrcode,300))

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

    //用户操作监听
    override fun onUserInteraction() {
        Constance.lastTouchTime = System.currentTimeMillis()
        //重新倒计时
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "")!!.toString())) {
            countDownTimer.cancel()
        } else {
            countDownTimer.cancel()
            countDownTimer.start()
        }
        super.onUserInteraction()
    }

    //显示或隐藏登录
    fun isLogining(){
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info,"").toString())){
            arl_index_function_member_center.visibility=View.GONE
            tv_index_function_member_center.visibility=View.VISIBLE

            tv_index_function_username.text=""
            countDownTimer.cancel()

            handler.postDelayed(Runnable {
                //开启登录识别扫描器
                startLoginRecognitionScan()
                //创建新的微信授权二维码
                CreateScanCode(SharePerferenceUtil.getInstance()
                        .getValue(Constance.MachineID, "").toString())
            },500)
        }else{
            //显示登录信息
            arl_index_function_member_center.visibility=View.VISIBLE
            tv_index_function_member_center.visibility=View.GONE
            var userdata=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),JsonObject::class.java)
            tv_index_function_username.text="会员在线 ! "+userdata!!.get("CustName")?.asString
            countDownTimer.cancel()
            countDownTimer.start()
        }
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        wx_handle.removeCallbacksAndMessages(null)
        MyApplication.getInstance().removeActivity(this)
        handler.removeCallbacks(noTouchRunnable)
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        countDownTimer.cancel()
        handler.removeCallbacks(noTouchRunnable)
    }

    override fun onPause() {
        //停止扫描
        VbarUtils.getInstance(this).stopScan()
        countDownTimer.cancel()
        wx_handle.removeCallbacksAndMessages(null)
        handler.removeCallbacks(noTouchRunnable)
        super.onPause()
    }

    private fun updateVersion() {
        NetworkRequest.getInstance().getVersion(object : NetworkCallback<Version>() {
            override fun onSuccess(data: Version) {
                val pm = mContext.getPackageManager()
                var pi: PackageInfo? = null
                var versionCode = 0
                try {
                    pi = pm.getPackageInfo(mContext.getPackageName(), 0)
                    versionCode = pi!!.versionCode
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

                if (versionCode < data.versionid) {
                    SharePerferenceUtil.getInstance().setValue(Constance.auto_Install, true)
                    val file = File(FileManager.getInstance().downFileDir + "newapp" + data.versionid + ".apk")
                    if (file.exists() && SharePerferenceUtil.getInstance().getValue("newapp" + data.versionid, false) as Boolean) {

                        AntoUtil.setUrl(FileManager.getInstance().downFileDir + "newapp" +
                                data.versionid + ".apk")
                        AntoUtil.install(mContext as Activity)

                        return
                    }
                    val down = DownFileUtil(null)
                    down.downApk(data.url, data.versionid, mContext as Activity)
                } else {
                    //Toast.makeText(MallActivity.this,"已是最新版本",Toast.LENGTH_SHORT).show();
                }
            }

            override fun onFailure(code: Int, msg: String) {}
        })
    }

    fun getCranema() {
        NetworkRequest.getInstance().getCranemaapi(cinemaType, cinemaId, object : NetworkCallback<Cranemaapi>() {
            override fun onSuccess(data: Cranemaapi) {
                val gson = Gson()
                val json = gson.toJson(data)
                SharePerferenceUtil.getInstance().setValue("cranemaapi", json)
                SharePerferenceUtil.getInstance().setValue("home_page", data.home_page)
            }

            override fun onFailure(code: Int, msg: String) {

            }

        })
    }

    fun itemVideo() {
        var map=HashMap<String,String>()
        map.put("shop_id",cinemaType)
        NetworkRequest.getInstance().itemVideo(map,object : NetworkCallback<JsonObject>() {
            override fun onSuccess(data: JsonObject?) {
                if(data==null){
                    return
                }
                SharePerferenceUtil.getInstance().setValue(Constance.itemVideoUrl, data.toString())

                var videoUrlList=GsonUtil.jsonToList(data!!.get("videodata").asJsonArray.toString(),String::class.java)
                //下载视频
                handler.postDelayed(Runnable {
                    val downFileUtil = DownFileUtil(videoUrlList)
                    downFileUtil.startDown()
                },8000)

            }

            override fun onFailure(code: Int, msg: String) {

            }

            override fun onNetWorkFailure(e: Exception?) {
                handler.postDelayed(Runnable { itemVideo() },8000)
                super.onNetWorkFailure(e)
            }

            override fun onCompleted() {
                //轮播视频
                playVideo()
                super.onCompleted()
            }

        })
    }


    //播放视频
    private fun playVideo() {
        var videoUrl=SharePerferenceUtil.getInstance().getValue(Constance.itemVideoUrl,"").toString()
        if(TextUtils.isEmpty(videoUrl)){
            return
        }
        var videoUrlJson=GsonUtil.jsonToObject(videoUrl,JsonObject::class.java)
        videoSize=videoUrlJson!!.get("videodata").asJsonArray.size()
        val fileName = FileManager.getInstance().getFileName(videoUrlJson!!.get("videodata").asJsonArray.get(videoIndex).asString)
        var uri: Uri? = null
        if (FileManager.getInstance().isFileExists(fileName)) {
            uri = Uri.parse(FileManager.getInstance().destFileDir + fileName)
        } else {
            uri = Uri.parse(videoUrlJson!!.get("videodata").asJsonArray.get(videoIndex).asString)
        }
        mvv_index_function_videoView.setOnCompletionListener {
            mp->
            if (mp!=null){
                mp.setDisplay(null);
                mp.reset();
                mp.setDisplay( mvv_index_function_videoView.holder)
            }
            videoIndex++
            if (videoIndex >= videoSize) {
                videoIndex = 0
            }
            playVideo()
        }
        mvv_index_function_videoView.setOnErrorListener { mediaPlayer, i, i1 ->
            if (FileManager.getInstance().isFileExists(fileName)) {
                val file = File(FileManager.getInstance().destFileDir + fileName)
                file.delete()
                playVideo()
            } else {
                handler.postDelayed(videoRunnable, (8 * 1000).toLong())
            }
            true
        }

        mvv_index_function_videoView.setOnInfoListener { mediaPlayer, i, i1 ->
            handler.removeCallbacks(videoRunnable)
            false
        }
        //设置视频路径
        mvv_index_function_videoView.setVideoURI(uri)
        //开始播放视频
        mvv_index_function_videoView.start()
//        handler.postDelayed(videoRunnable, (60 * 1000).toLong())
    }

    private val videoRunnable = Runnable {
        videoIndex++
        if (videoIndex >=videoSize) {
            videoIndex = 0
        }
        playVideo()
    }

    /**
     * 用户2分钟没操作执行
     */
    private var noTouchRunnable = object : Runnable {
        override fun run() {

            if (System.currentTimeMillis() - Constance.lastTouchTime >= 40*1000) {
                var home_page=SharePerferenceUtil.getInstance().getValue("home_page","1").toString()
                when(home_page){
                    //购币页面
                    "2"->{
                        //返回首页
                        startActivity(Intent(this@IndexFuncitonActivity, BuyCoinsActivity::class.java))
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

                    }
                    //商城页面
                    "3"->{
                        //返回首页
                        startActivity(Intent(this@IndexFuncitonActivity, MallActivity::class.java))
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

                    }
                    //首页
                    "1"->{

                    }
                    //其他
                    else->{

                    }
                }

            }
            handler.postDelayed(this, (30 * 1000).toLong())
        }
    }



    /**
     * 更改机器登录状态
     */
    fun getMachineLogout(MachineID:String){
        var hashmap= HashMap<String,String>()
        hashmap.put("MachineID",MachineID)
        hashmap.put("IsLogOut",false.toString())
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.MachineLogout,hashmap,object:XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result,JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200"){

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

    /**
     * 是否已经登录商城
     */
    private fun isAbleShop(): Boolean {
        return when {
            TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                    .getValue(Constance.member_Info, "")!!.toString()) -> {

                CToast("请登录")
                false
            }
            TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                    .getValue(Constance.user_accessToken, "")!!.toString()) -> {

                CToast("请关注公众号绑卡或到前台添加手机号码,再重新登录!")
                false
            }
            else -> true
        }

    }
}
