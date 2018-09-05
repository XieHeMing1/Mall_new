package com.qy.zgz.mall.page.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseActivity
import com.qy.zgz.mall.network.Constance
import com.qy.zgz.mall.page.index_function.IndexFuncitonActivity
import com.qy.zgz.mall.utils.SharePerferenceUtil
import kotlinx.android.synthetic.main.activity_setting_host.*
import org.xutils.view.annotation.ContentView

@ContentView(R.layout.activity_setting_host)
class SettingHostActivity : BaseActivity() {
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_setting_host_yes->{
                SharePerferenceUtil.getInstance().setValue(Constance.MEMBER_HOST_TAG,et_setting_host_hostname.text.toString())
                startActivity(Intent(this,IndexFuncitonActivity::class.java))
                finish()
            }
        }
    }

    override fun init(savedInstanceState: Bundle?) {
        et_setting_host_hostname.setText(SharePerferenceUtil.getInstance().getValue(Constance.MEMBER_HOST_TAG,"").toString())
    }

    override fun ObjectMessage(msg: Bundle?) {
    }


}
