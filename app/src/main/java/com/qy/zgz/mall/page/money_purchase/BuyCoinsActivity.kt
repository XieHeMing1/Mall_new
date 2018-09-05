package com.qy.zgz.mall.page.money_purchase


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Dbsql.DBDao
import com.qy.zgz.mall.Dbsql.DBOutCoinRecord
import com.qy.zgz.mall.Dbsql.DBReceiveMoneyRecord
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.SerialPortListener
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd
import com.qy.zgz.mall.Model.BuyCoins
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseReadCardFragmentActivity
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.page.index.MallActivity
import com.qy.zgz.mall.page.index_function.IndexFuncitonActivity
import com.qy.zgz.mall.page.money_purchase.error_handle.ErrorHandleActivity
import com.qy.zgz.mall.page.money_purchase.purchase_record.PurchaseRecordFragment
import com.qy.zgz.mall.page.money_purchase.pwd_update.PwdUpdateFragment
import com.qy.zgz.mall.page.money_purchase.take_coin.TakeCoinsFragment
import com.qy.zgz.mall.utils.*
import com.qy.zgz.mall.widget.TisCashPayDialog
import com.qy.zgz.mall.widget.TisDialog
import com.qy.zgz.mall.widget.TisEditDialog
import com.qy.zgz.mall.widget.TisOutCoinsDialog
import kotlinx.android.synthetic.main.activity_buy_coins.*
import org.xutils.common.Callback
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class BuyCoinsActivity : BaseReadCardFragmentActivity(),SerialPortListener {



    //是否成功打开串口
    var isSuccessOpenSerial:Boolean=false

    //判断是否可以收现金
    var canReceiveMoney:Boolean=false

    var outCoins_dialog:TisOutCoinsDialog?=null

    var StockBillID=""

    var cashDialog:TisCashPayDialog?=null

    var isShowAutoBuy:Boolean=false//是否显示自由购买 ,1--显示，0--不显示

    var lastCashBuyCoins=ArrayList<BuyCoins>() //（现金购买时）需要更新的套餐信息

//    var outNoReceiveTimeTaker=object:TimerTask(){
//        override fun run() {
//            //结束本轮售币任务
//            kd.sp().bdCoinOuted()
//            kd.sp().bdCleanError()
//            //执行更新数据方法
//            takeFailUpdateOutCoinLog(0);
//            if(null!=outCoins_dialog){
//                outCoins_dialog!!.showBug(View.VISIBLE)
//            }
//        }
//
//    }


  override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_buy_coins)


        var typeid=SharePerferenceUtil.getInstance().getValue("typeId","").toString()
        if (typeid=="7"){
            //天河城店铺
            arl_buy_coins_qrcode.visibility=View.INVISIBLE
            tv_buy_coins_login_way.text="请刷卡登录"
        }else{
            arl_buy_coins_qrcode.visibility=View.VISIBLE
            tv_buy_coins_login_way.text="请刷卡/扫码登录"
        }

      //欢乐熊版本 店铺id:25
      if(typeid=="25"){
          arl_buy_coins_take.visibility=View.GONE
      }

        tv_mainename.text=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassNAME,"").toString()

//        iv_buy_coins_go.isSelected=true
//        supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment,BuyCoinsFragment()).commit()

        v_buy_coins_clean_error.setOnLongClickListener {
           UnityDialog(this).setHint("是否清除出币故障？")
                   .setCancel("取消",null)
                   .setConfirm("确定",object:UnityDialog.OnConfirmDialogListener{
                       override fun confirm(unityDialog: UnityDialog?, content: String?) {
                           kd.sp().bdCleanError()
                           kd.sp().bdCoinOuted()
                           kd.sp().isSuccessOutCoin=true
                            unityDialog!!.dismiss()
                       }

                   })

            false
        }
    }

    override fun onResume() {
        super.onResume()
        kd.sp().go(this)
        //本地出币记录更新数据库记录
        UpdateServerByLoalData()
        getGMSSettingsInfoList()
        onClick(iv_buy_coins_go)
        handler.removeCallbacks(noTouchRunnable)
        handler.postDelayed(noTouchRunnable,60*1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(noTouchRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(noTouchRunnable)
    }

    //倒计时进行中的View处理
    override fun onTickView(millisUntilFinished: Long) {
        var time=(millisUntilFinished/1000).toString()
        if (time.length==1){
            time="0"+time
        }
        tv_buy_coins_time.text="倒计时：00:"+time
    }

    //倒计时完成时的View处理
    override fun onTickViewFinish() {
        Log.e("ttt","tt")
        tv_buy_coins_time.text="倒计时：00:00"
    }

    //登录中的View处理
    override fun initLoginView() {
        all_buy_coin_logining.visibility=View.VISIBLE
        all_buy_coin_unlogin.visibility=View.GONE

        var userdata= GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), JsonObject::class.java)
        tv_buy_coins_username.text=userdata!!.get("CustName").asString
        tv_buy_coins_userno.text="编号 : "+userdata!!.get("Number").asString
        tv_buy_coins_userlevle.text="等级 : "+userdata!!.get("LevelName").asString
        tv_buy_coins_userticket.text="彩票:"+userdata!!.get("Tickets")?.asString
        tv_buy_coins_usercoins.text="游戏币:"+userdata!!.get("Coins")?.asString
        tv_buy_coins_userpoint.text="积分:"+userdata!!.get("Point")?.asString
        tv_buy_coins_userdepoit.text="预存款:"+userdata!!.get("Money")?.asString
        tv_buy_coins_userrecoin.text="代币数:0"

        iv_login_exit.setImageResource(R.drawable.bg_btn_logining)

        if (iv_buy_coins_go.isSelected){
            onClick(iv_buy_coins_go)
        }
    }

    //未登录中的View处理
    override fun initUnLoginView() {
        all_buy_coin_logining.visibility=View.GONE
        all_buy_coin_unlogin.visibility=View.VISIBLE

        tv_buy_coins_time.text="倒计时：00:00"

        iv_login_exit.setImageResource(R.drawable.out_btn)

        when {
            iv_buy_coins_go.isSelected -> onClick(iv_buy_coins_go)
            iv_purchase_records_go.isSelected -> onClick(iv_buy_coins_go)
            iv_pwd_update_go.isSelected -> onClick(iv_buy_coins_go)
        }
    }

    //显示登录二维码
    override fun showLoginQRcode(qrcode: String) {
        iv_buy_coins_qrcode.setImageBitmap(QRBitmapUtils.createQRCode(qrcode,300))
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //购币
            R.id.iv_buy_coins_go->{
//                var dialog=TisDialog(this).create().setMessage("暂未开放！").show()

                adjustButtonBg(iv_buy_coins_go)
                supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment,BuyCoinsFragment()).commitNowAllowingStateLoss()

            }
        //提取
            R.id.iv_take_coins_go->{
                adjustButtonBg(iv_take_coins_go)
                supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment, TakeCoinsFragment()).commit()

            }
        //查看消费记录
            R.id.iv_purchase_records_go->{
                if (isMemberLogining()) {
                    adjustButtonBg(iv_purchase_records_go)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment, PurchaseRecordFragment()).commit()
                }else{
                    var dialog=TisDialog(this).create().setMessage("请先登录！").show()
                }
            }
        //修改密码
            R.id.iv_pwd_update_go->{
                if (isMemberLogining()) {
                    adjustButtonBg(iv_pwd_update_go)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_buy_coins_fragment,PwdUpdateFragment()).commit()
                }else{
                    var dialog=TisDialog(this).create().setMessage("请先登录！").show()
                }
            }
        //退出登录按钮
            R.id.iv_login_exit->{
                if (!isMemberLogining()){
                    return
                }
                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info,"")
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken,"")
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id,"")

                onResume()

                TisDialog(this).create().setMessage("退出成功！").show()
            }
            R.id.iv_buy_coins_login_finish->{
                startActivity(Intent(this, IndexFuncitonActivity::class.java))
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

            }
            R.id.iv_buy_coins_unlogin_finish->{
                startActivity(Intent(this, IndexFuncitonActivity::class.java))
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

            }
            R.id.iv_buy_coins_login_go_shop->{
                startActivity(Intent(this, MallActivity::class.java))
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

            }
            R.id.iv_buy_coins_unlogin_go_shop->{
                startActivity(Intent(this, MallActivity::class.java))
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

            }
            //经理卡进去异常管理系统
            R.id.iv_buy_coins_manger_setting->{
                if (isMemberLogining()) {
                    var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)
                   if(memberInfo!=null&&memberInfo!!.LevelID==Constance.machineMangerLevel){

                       var  editDialog=TisEditDialog(this).create().setMessage("请输入密码")
                               .setNegativeButton {  }
                               .setPositiveButton{
                                   v,pwd->
                                   checkMemberPwd(memberInfo!!.Id,pwd)
                               }.show()


                   }
                }
            }
        }
    }

    override fun getMessage(bundle: Bundle) {
    }

    //调整按钮组背景
    fun adjustButtonBg(v:View){
        iv_buy_coins_go.isSelected=false
        iv_take_coins_go.isSelected=false
        iv_pwd_update_go.isSelected=false
        iv_purchase_records_go.isSelected=false
        v.isSelected=true
    }

    /**
     * 检查会员密码
     */
    fun  checkMemberPwd(CustID:String,PassWord:String){
        if (PassWord.isNullOrEmpty()){
            var dialog=TisDialog(this).create().setMessage("密码不能为空").show()
            return
        }
        var hashmap=HashMap<String,String>()
        hashmap.put("CustID",CustID)
        hashmap.put("PassWord",PassWord)
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.CheckCustomerPassword,hashmap,object :XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200") {

                    startActivity(Intent(this@BuyCoinsActivity, ErrorHandleActivity::class.java))
                    // 定义出入场动画
                    overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

                }else{
                    var dialog=TisDialog(this@BuyCoinsActivity).create().setMessage(rjson!!.get("result_Msg")?.asString?:"错误").show()

                }

            }
            override fun onCancelled(cex: Callback.CancelledException?) {

            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                var dialog=TisDialog(this@BuyCoinsActivity).create().setMessage("网络故障").show()

            }

            override fun onFinished() {

            }
        })
    }


    /**
     * 获取一体机参数
     */
    fun  getGMSSettingsInfoList(){
        var hashmap=HashMap<String,String>()
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.GetGMSSettingsInfoList,hashmap,object :XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200") {
                    var result=rjson!!.getAsJsonArray("Data")
                    result.forEach {
                        if (it.asJsonObject.get("SettingKey").asString=="GMSGetCoinLimit"){
                            Constance.maxOutCoinValue=if(TextUtils.isEmpty(it.asJsonObject.get("Value").asString)){
                                200 //默认200
                            }else{
                                it.asJsonObject.get("Value").asInt
                            }

                        }

                        if(it.asJsonObject.get("SettingKey").asString=="GMSAutoSale"){
                            isShowAutoBuy=if(TextUtils.isEmpty(it.asJsonObject.get("Value").asString)){
                                false
                            }else{
                                it.asJsonObject.get("Value").asString=="1"
                            }
                        }
                    }

                }else{
                    Constance.maxOutCoinValue=200
                }

            }
            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                Constance.maxOutCoinValue=200
            }

            override fun onFinished() {
            }
        })
    }


    //跳转到提币界面
    fun goTakeCoin(){
        onClick(iv_take_coins_go)
    }

    /**
     * 用户2分钟没操作执行
     */
    private var noTouchRunnable = object : Runnable {
        override fun run() {
            if (System.currentTimeMillis() - Constance.lastTouchTime >= 60*1000) {
                var home_page=SharePerferenceUtil.getInstance().getValue("home_page","1").toString()
                when(home_page){
                //首页
                    "1"->{
                        //返回首页
                        startActivity(Intent(this@BuyCoinsActivity, IndexFuncitonActivity::class.java))
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

                    }
                //购币页面
                    "2"->{
                        onClick(iv_buy_coins_go)
                    }
                //商城页面
                    "3"->{
                        //返回首页
                        startActivity(Intent(this@BuyCoinsActivity, MallActivity::class.java))
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)

                    }
                    else ->{
                        //返回首页
                        startActivity(Intent(this@BuyCoinsActivity, IndexFuncitonActivity::class.java))
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)
                    }
                }

            }
            handler.postDelayed(this, (30 * 1000).toLong())
        }
    }

    /**
     * 退出账号
     */
    fun exitMember(){
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
     * 关闭显示出币界面
     */
    fun closeOutCoinDialog(){
        exitMember()
        handler.postDelayed(Runnable {
            if(null!=outCoins_dialog){

                outCoins_dialog!!.dismiss()

            }
        },3000)
    }

    /**
     * 显示出币界面
     */
    fun outCoins(num:Int){

        outCoins_dialog = if(num>200){
            TisOutCoinsDialog(this).create().setTotalNum(num.toString() + "")
                    .setNum("0").showContiune(View.VISIBLE).show()
        }else{
            TisOutCoinsDialog(this).create().setTotalNum(num.toString() + "")
                    .setNum("0").show()
        }

        kd.sp().bdSendOutCoin(num,kd.sp().getDevice("3"),1)
}

    //--串口-----
    override fun onCoinOuting(count: Int) {

        //存入本地数据
        saveLocalOutCoinRecord(count)

        if(null!=outCoins_dialog){
            outCoins_dialog!!.setNum(count.toString())
        }


    }

    override fun onCoinOutSuccess(count: Int) {
        //存入本地数据
        saveLocalOutCoinRecord(count)

        if(null!=outCoins_dialog){
            outCoins_dialog!!.showContiune(View.GONE)
        }
        takeUpdateOutCoinLog(count)
        closeOutCoinDialog()
    }

    override fun onCoinOutFail(outCount: Int, count: Int, errorCode: String?) {
        if (count>=0){
            //存入本地数据
            saveLocalOutCoinRecord(count)

            Log.e("-out-",outCount.toString()+"---"+count.toString())
            takeFailUpdateOutCoinLog(outCount)
            if(null!=outCoins_dialog){
                outCoins_dialog!!.showContiune(View.GONE)
                outCoins_dialog!!.showBug(View.VISIBLE)
            }
        }



//        closeOutCoinDialog()
    }

    override fun onReceivedMomey(amount: Int, macType: String) {

        if (canReceiveMoney && isSuccessOpenSerial && kd.sp().isSuccessOutCoin) {

            if (cashDialog!=null&&cashDialog!!.isShowing){
                //初始化需要更新的套餐信息
                lastCashBuyCoins.clear()

                try {
                    if (cashDialog!!.shouldPrice.toDouble()<=0){
                        //退钱指令
                        kd.sp().sendOutMomeyCmd(macType)
                        cashDialog!!.dismiss()
                        return
                    }

                }catch (e:Exception){
                    //退钱指令
                    kd.sp().sendOutMomeyCmd(macType)
                    cashDialog!!.dismiss()
                    return
                }

                cashDialog!!.freshCountDown()

                //判断是否超出应收金额
                if (cashDialog!!.hadPrice.toDouble()+amount>cashDialog!!.shouldPrice.toDouble()){
                    autoMathPackageList((cashDialog!!.hadPrice.toDouble()+amount).toString(),macType)
                }else{
                    //收钱指令
                    kd.sp().sendGetMomeyCmd(macType)
                }

            }else{
                //退钱指令
                kd.sp().sendOutMomeyCmd(macType)
            }
        }else{
            if (!canReceiveMoney){
                TisDialog(this).create()
                        .setMessage("纸钞机异常，请联系管理员").show()
            }else{
                TisDialog(this).create()
                        .setMessage("币斗没币或异常，请联系管理员").show()
            }

        }
    }

    override fun onReceivedMomeySuccess(amount: Int, macType: String) {
        Log.e("Money","ssss")
        if (cashDialog!=null&&cashDialog!!.isShowing){


            if (lastCashBuyCoins!=null && lastCashBuyCoins.isNotEmpty()){
                Log.e("Money","超出刷新")
                cashDialog!!.setInfo(lastCashBuyCoins)
            }

            try {
                cashDialog!!.hadPrice = (cashDialog!!.hadPrice.toDouble()+amount).toString()
            }catch (e:Exception){
                cashDialog!!.hadPrice = amount.toString()
            }

            //存入本地数据
            var id=saveLocalCashRecord(cashDialog!!.hadPrice.toDouble(), cashDialog!!.localId)
            cashDialog!!.localId = id

            if(cashDialog!!.hadPrice.toDouble()>=cashDialog!!.shouldPrice.toDouble()){
                cashDialog!!.showOutSaveButton()
            }

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
        Log.e("spo","fa")
    }

    override fun onSerialPortOpenSuccess(file: File?) {
        Log.e("spo","su")
    }

    override fun onMachieConnectedSuccess(device: String?) {
        //币斗
        if (device!!.contains(kd.sp().getDevice("3"))){
        isSuccessOpenSerial=true
            Log.e("MA","su")
        }
        Log.e("MADV",device!!.toString())
        //纸币机
        if (device!!.contains(kd.sp().getDevice("1"))||device!!.contains(kd.sp().getDevice("2"))){
            canReceiveMoney=true
            try{
                kd.sp().colseBanknote()
            }catch (e:Exception){

            }
        }

    }

    override fun onMachieCommectedFail(device: String?) {
        //币斗
        if (device!!.contains(kd.sp().getDevice("3"))){
            isSuccessOpenSerial=false
            Log.e("MA","FA")
        }
        Log.e("MA2",device!!.toString())
        //纸币机
        if (device!!.contains(kd.sp().getDevice("1"))||device!!.contains(kd.sp().getDevice("2"))){
            canReceiveMoney=false
        }



    }


    //提币更新数据接口
    fun takeUpdateOutCoinLog(num:Int){
        var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java)

        var hashmap=HashMap<String,String>()
        hashmap.put("OutCoins",num.toString())
        hashmap.put("StockBillID",StockBillID)

        if (null==memberInfo){
            hashmap.put("CustID",Constance.machineFLTUserID)
            hashmap.put("IsSaveCard",false.toString())
        }else{
            hashmap.put("CustID",memberInfo!!.Id)
            hashmap.put("IsSaveCard",true.toString())
        }

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


                }else{

                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                StockBillID=""

            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {

            }

        })

    }



    //提币更新数据接口
    fun takeFailUpdateOutCoinLog(num:Int){
        var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(), MemberInfo::class.java)

        var hashmap=HashMap<String,String>()
        hashmap.put("OutCoins",num.toString())
        hashmap.put("StockBillID",StockBillID)

        if (null==memberInfo){
            hashmap.put("CustID",Constance.machineFLTUserID)
            hashmap.put("IsSaveCard",false.toString())
        }else{
            hashmap.put("CustID",memberInfo!!.Id)
            hashmap.put("IsSaveCard",true.toString())
        }

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

                    if(isMemberLogining()){
                        kd.sp().bdCleanError()
                        outCoins_dialog!!.setBugText("机器没币，未出的币已经返还卡中！")

                        exitMember()
                        handler.postDelayed(Runnable {
                            if(null!=outCoins_dialog){
                                outCoins_dialog!!.dismiss()

                            }
                        },8000)
                    }else{
                        if (null!=outCoins_dialog&&outCoins_dialog!!.isShowing){
                            outCoins_dialog!!.setBugText("机器没币，请联系管理员补币")
                            outCoins_dialog!!.showClose()
                        }
                    }

                }else{
                    if (null!=outCoins_dialog&&outCoins_dialog!!.isShowing){
                        outCoins_dialog!!.setBugText("机器故障或没币，请联系管理员补币")
                        outCoins_dialog!!.showClose()
                    }
                }
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onFinished() {
                StockBillID=""
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                if (null!=outCoins_dialog&&outCoins_dialog!!.isShowing){
                    outCoins_dialog!!.setBugText("机器故障，请联系管理员补币")
                    outCoins_dialog!!.showClose()
                }
            }

        })

    }


    /**
     * 通过ID刷新会员信息
     * 更新会员信息需要注意是扫卡登录还是刷卡登录
     */
    fun updateMenterInfo() {
        var memberInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)
        if(null==memberInfo){
            isLogining()
            return
        }
        val hashmap = HashMap<String, String>()
        hashmap.put("CustID", memberInfo!!.Id)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        val dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetMemberInfoByCardNo, hashmap, object : XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
                val jsonResult = GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (jsonResult!!.has("return_Code") && jsonResult.get("return_Code").toString() == "200" &&
                        jsonResult.get("Data").asJsonObject.get("Status").asString == "0") {
                    val data = jsonResult.getAsJsonObject("Data")


                    var memberInfo_new=GsonUtil.jsonToObject(data.toString(),MemberInfo::class.java)

                    //是否扫卡登录
                    memberInfo_new!!.IsScan=memberInfo!!.IsScan

                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, GsonUtil.objectToJson(memberInfo_new))

                    //显示登录信息
                    isLogining()


                } else {
                    exitMember()
                    var dialog=TisDialog(this@BuyCoinsActivity).setMessage("刷新失败,请重新登录查看最新信息!").show()

                }

            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {
                exitMember()
                var dialog=TisDialog(this@BuyCoinsActivity).setMessage("刷新失败,请重新登录查看最新信息!").show()

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
    fun autoMathPackageList(price:String,macType:String) {
        val dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show()
        val memberInfo = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "")!!.toString(), MemberInfo::class.java)

        val hashmap = HashMap<String, String>()

        hashmap.put("PackageType", "Pa01")
        hashmap.put("MachineID", SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "")!!.toString())

        if (null == memberInfo) {
            hashmap.put("CustID", Constance.machineFLTUserID)
        } else {
            hashmap.put("CustID", memberInfo.Id)
        }

        hashmap.put("Amount", price)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.AutoMathPackageList, hashmap, object : XutilsCallback<String>() {
            override fun onSuccessData(result: String) {
                val rjson = GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code") && rjson.get("return_Code").asString == "200"
                        && rjson.get("Data").asJsonArray.size()>0) {

                    lastCashBuyCoins=GsonUtil.jsonToList(rjson.get("Data").asJsonArray.toString(),BuyCoins::class.java) as ArrayList<BuyCoins>

                    //收钱指令
                    kd.sp().sendGetMomeyCmd(macType)

                } else {
                    //退钱指令
                    kd.sp().sendOutMomeyCmd(macType)
                }
            }

            override fun onError(ex: Throwable, isOnCallback: Boolean) {
                //"退钱指令"
                kd.sp().sendOutMomeyCmd(macType)

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
     * 存入本地数据库（出币数据）
     */
    fun saveLocalOutCoinRecord(count:Int){
        var memberInfo = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "")!!.toString(), MemberInfo::class.java)
        //存入本地数据库
        var outcointRecord:DBOutCoinRecord= DBOutCoinRecord()
        outcointRecord.outcount=count
        outcointRecord.isError=1
        outcointRecord.stockBillID=StockBillID
        if (memberInfo!=null){
            outcointRecord.custID=memberInfo!!.Id
            outcointRecord.custName=memberInfo!!.CustName
        }else{
            outcointRecord.custID=Constance.machineFLTUserID
            outcointRecord.custName="大众会员"
        }
        outcointRecord.classId=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString()
        outcointRecord.classTime=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString()


        DBDao.getInstance().saveOutCoinsRecord(outcointRecord)
    }


    /**
     * 存入本地数据库（现金数据）
     */
    fun saveLocalCashRecord(money:Double,localId:Int):Int{
        var memberInfo = GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "")!!.toString(), MemberInfo::class.java)
        //存入本地数据库
        var moneyRecord:DBReceiveMoneyRecord= DBReceiveMoneyRecord()
        moneyRecord.id=localId
        moneyRecord.money=money
        moneyRecord.isError=1
        moneyRecord.classId=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassID,"").toString()
        moneyRecord.classTime=SharePerferenceUtil.getInstance().getValue(Constance.MachineClassTime,"").toString()
        if (memberInfo!=null){
            moneyRecord.custID=memberInfo!!.Id
            moneyRecord.custName=memberInfo!!.CustName
        }else{
            moneyRecord.custID=Constance.machineFLTUserID
            moneyRecord.custName="大众会员"
        }

        return DBDao.getInstance().saveOrUpdateCashRecord(moneyRecord)
    }



    //本地出币记录更新数据库记录
    fun UpdateServerByLoalData(){
        var outListRecord=DBDao.getInstance().queryErrorBill()
        if (outListRecord==null||outListRecord.isEmpty()){
            return
        }
        var outList=ArrayList<HashMap<String,Any>>()
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
                    var sbillList:ArrayList<String> = ArrayList<String>()
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



    //显示纸币付款提示窗
    fun showCashPayDialog(buycoinsList:ArrayList<BuyCoins>){
        cashDialog=TisCashPayDialog(this).create()
                .setInfo(buycoinsList)
                .show()
    }

}
