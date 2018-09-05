package com.qy.zgz.mall.page.money_purchase.error_handle

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.gson.JsonObject
import com.kaopiz.kprogresshud.KProgressHUD
import com.qy.zgz.mall.Dbsql.DBDao
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseReadCardFragmentActivity
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity
import com.qy.zgz.mall.utils.*
import com.qy.zgz.mall.widget.TisDialog
import kotlinx.android.synthetic.main.activity_error_handle.*
import org.xutils.common.Callback
import java.util.*
import kotlin.collections.ArrayList


class ErrorHandleActivity : BaseReadCardFragmentActivity() {

    var ClearID=""
    var ClearNum="0"

    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_error_handle)

        setSOrECountDown(false)

//        OrderUpdate()

        //更新出币记录
        UpdateServerByLoalData()

    }

    override fun initLoginView() {

    }

    override fun initUnLoginView() {

    }

    override fun onTickView(millisUntilFinished: Long) {
//        var time=(millisUntilFinished/1000).toString()
//        if (time.length==1){
//            time="0"+time
//        }
//        tv_error_handle_time.text="倒计时：00:"+time
    }

    override fun onTickViewFinish() {
//        tv_error_handle_time.text="倒计时：00:00"
//        finish()
    }

    override fun showLoginQRcode(qrcode: String) {
    }



    override fun onClick(v: View?) {
        when(v!!.id){
            //异常处理
            R.id.iv_error_handle_yccl->{
                adjustButtonBg(iv_error_handle_yccl)
                supportFragmentManager.beginTransaction().replace(R.id.fl_error_handle_fragment, ExceptionHandingFragment()).commitNowAllowingStateLoss()

            }
            //交班
            R.id.iv_error_handle_jb->{

                adjustButtonBg(iv_error_handle_jb)
                supportFragmentManager.beginTransaction().replace(R.id.fl_error_handle_fragment, ChangeShiftsFragment()).commitNowAllowingStateLoss()

            }
            R.id.iv_error_handle_exit->{
                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info,"")
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken,"")
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id,"")

                finish()
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right)
                startActivity(Intent(this,BuyCoinsActivity::class.java))


            }

            //清楚故障
            R.id.iv_error_handle_clearbug->{
                UnityDialog(this).setHint("是否清除出币故障？")
                        .setCancel("取消",null)
                        .setConfirm("确定",object: UnityDialog.OnConfirmDialogListener{
                            override fun confirm(unityDialog: UnityDialog?, content: String?) {
                                kd.sp().bdCoinOuted()
                                kd.sp().bdCleanError()
                                kd.sp().isSuccessOutCoin=true
                                unityDialog!!.dismiss()
                            }

                        })
            }
            //关机
            R.id.iv_error_handle_close_system->{
                UnityDialog(this).setHint("是否确定关机？")
                        .setCancel("取消",null)
                        .setConfirm("确定",object: UnityDialog.OnConfirmDialogListener{
                            override fun confirm(unityDialog: UnityDialog?, content: String?) {
                                getMachineLogout()
//                                MyApplication.getInstance().finishActivity()
                                unityDialog!!.dismiss()
                            }

                        })

            }
            //重启
            R.id.iv_error_handle_reboot_system->{
                UnityDialog(this).setHint("是否确定重启？")
                        .setCancel("取消",null)
                        .setConfirm("确定",object: UnityDialog.OnConfirmDialogListener{
                            override fun confirm(unityDialog: UnityDialog?, content: String?) {
                                ShutDownUtil.reboot()
                                unityDialog!!.dismiss()
                            }

                        })

            }
        }
    }

    override fun getMessage(bundle: Bundle) {
    }

    //调整按钮组背景
    fun adjustButtonBg(v:View){
        iv_error_handle_jb.isSelected=false
        iv_error_handle_yccl.isSelected=false
        v.isSelected=true
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
     * 同步机器订单
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
     * 更改机器登录状态
     */
    fun getMachineLogout(){
        val dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("正在关机,请稍后...").show()
        var hashmap= HashMap<String,String>()
        hashmap.put("MachineID",SharePerferenceUtil.getInstance().getValue(Constance.MachineID,"").toString())
        hashmap.put("IsLogOut",true.toString())
        hashmap.put("sign",SignParamUtil.getSignStr(hashmap))
        HttpUtils.xPostJson(Constance.MEMBER_HOST+Constance.MachineLogout,hashmap,object:XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson=GsonUtil.jsonToObject(result,JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200"){

                }
            }

            override fun onFinished() {
                if (dia != null && dia.isShowing) {
                    dia.dismiss()
                }
                ShutDownUtil.shutdown()
            }

            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
            }
        })

    }

    //本地出币记录更新数据库记录
    fun UpdateServerByLoalData(){
        val dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("正在同步记录,请稍后...").show()
        var outListRecord= DBDao.getInstance().queryErrorBill()
        if (outListRecord==null||outListRecord.isEmpty()){
            if (dia != null && dia.isShowing) {
                dia.dismiss()
            }
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
                    TisDialog(this@ErrorHandleActivity).create().setMessage(rjson.get("result_Msg").asString).show()

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
                TisDialog(this@ErrorHandleActivity).create().setMessage("网络异常").show()

            }

        })

    }



}
