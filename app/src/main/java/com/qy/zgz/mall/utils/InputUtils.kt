package com.qy.zgz.mall.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/**
 * Created by LCB on 2018/2/28.
 */
class InputUtils {
    companion object {
        /**
         * 隐藏键盘
         */
        fun closeInput(context:Context) {
            try {
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                if (inputMethodManager!!.isActive) {
                    inputMethodManager.hideSoftInputFromWindow(
                            (context as Activity).currentFocus!!
                                    .windowToken, 0)
                }
            } catch (e: Exception) {
                // TODO: handle exception
            }

        }
    }
}