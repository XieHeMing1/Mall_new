package com.qy.zgz.mall.widget

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher

/**
 * Created by LCB on 2018/6/1.
 * 自定义监听输入框的输入内容
 */
open class MyTextWatch(var context:Context):TextWatcher {
    companion object{
        var instance:MyTextWatch?=null
        @JvmStatic fun getInstance(context:Context):MyTextWatch?{
            if (instance==null){
                instance= MyTextWatch(context)
            }

            return instance
        }

        //需在onResume里面调用一次
        @JvmStatic fun refresh(context:Context):MyTextWatch?{
                instance= MyTextWatch(context)
            return instance
        }

    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override  fun  onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        try {
            (context as Activity).onUserInteraction()
        }catch (e :Exception){

        }
    }
}