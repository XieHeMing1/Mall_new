package com.qy.zgz.mall.page.money_purchase.pwd_update

import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.gson.JsonObject
import com.qy.zgz.mall.Model.MemberInfo
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseFragment
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.network.XutilsCallback
import com.qy.zgz.mall.utils.GsonUtil
import com.qy.zgz.mall.utils.HttpUtils
import com.qy.zgz.mall.utils.SharePerferenceUtil
import com.qy.zgz.mall.utils.SignParamUtil
import org.xutils.common.Callback
import org.xutils.view.annotation.ContentView
import org.xutils.view.annotation.ViewInject
import java.util.*

@ContentView(R.layout.fragment_pwd_update)
class PwdUpdateFragment : BaseFragment() {

    @ViewInject(R.id.et_pwdupdate_npwd)
    lateinit var et_pwdupdate_npwd:EditText

    @ViewInject(R.id.et_pwdupdate_oldpwd)
    lateinit var et_pwdupdate_oldpwd:EditText

    @ViewInject(R.id.et_pwdupdate_cpwd)
    lateinit var et_pwdupdate_cpwd:EditText

    @ViewInject(R.id.btn_pwdupdate_comfire)
    lateinit var btn_pwdupdate_comfire:Button

    @ViewInject(R.id.btn_pwdupdate_cancel)
    lateinit var btn_pwdupdate_cancel:Button



    override fun init() {
        btn_pwdupdate_comfire.setOnClickListener(this)
        btn_pwdupdate_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            //确认修改
            R.id.btn_pwdupdate_comfire->{
                updateCustomerPwd(et_pwdupdate_oldpwd.text.toString(),et_pwdupdate_npwd.text.toString(),et_pwdupdate_cpwd.text.toString());
            }
        //确认修改
            R.id.btn_pwdupdate_cancel->{
                cleanET()
            }


        }
    }

    override fun ObjectMessage(msg: Message?) {
    }


    /**
     * 修改密码
     */
    fun  updateCustomerPwd(oPwd:String,nPwd:String,cPwd:String){
        var userInfo=GsonUtil.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString(),MemberInfo::class.java)
        var hashmap= HashMap<String,String>()
        hashmap.put("NewPassword", nPwd)
        hashmap.put("ConfirmPassword",cPwd)
        hashmap.put("OldPassword",oPwd)
        hashmap.put("CustID",userInfo!!.Id)
        hashmap.put("UserID",Constance.machineUserID)
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap))
        Log.i("XHM_TEST", "User ID = " + userInfo.Id);
        HttpUtils.xPostJson(Constance.MEMBER_HOST+ Constance.UpdateCustomerPwd,hashmap,object : XutilsCallback<String>(){
            override fun onSuccessData(result: String) {
                var rjson= GsonUtil.jsonToObject(result, JsonObject::class.java)
                if (rjson!!.has("return_Code")&& rjson!!.get("return_Code").asString == "200") {

                    cleanET()
                    CToast("修改成功!")
                }else{
                    CToast(rjson!!.get("result_Msg").asString)
                }

            }
            override fun onCancelled(cex: Callback.CancelledException?) {
            }

            override fun onError(ex: Throwable?, isOnCallback: Boolean) {
            }

            override fun onFinished() {
            }
        })
    }

    //清空输入框
    fun cleanET(){
        et_pwdupdate_cpwd.setText("")
        et_pwdupdate_npwd.setText("")
        et_pwdupdate_oldpwd.setText("")

    }


}