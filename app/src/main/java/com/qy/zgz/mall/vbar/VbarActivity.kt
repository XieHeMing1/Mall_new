package com.qy.zgz.mall.vbar

import android.os.Bundle
import android.view.View
import com.qy.zgz.mall.R
import com.qy.zgz.mall.base.BaseActivity


class VbarActivity : BaseActivity() {
    override fun init(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_vbar)
    }

    override fun ObjectMessage(msg: Bundle?) {
    }

    override fun onClick(v: View?) {
    }

    override fun onResume() {
        //开启扫描
        VbarUtils.getInstance(this).getScanResult()
        super.onResume()
    }




}
