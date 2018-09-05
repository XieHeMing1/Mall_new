package com.qy.zgz.mall.lcb_game

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.*
import android.widget.ImageView
import com.qy.zgz.mall.R

import com.zhy.autolayout.utils.AutoUtils

/**
 * Created by LCB on 2018/8/27.
 */

class GameLoginDialog(private val mcontext: Context?) {

    private var dialog: Dialog?
    private var contentview: View? = null

    private var dialog_countDown: CountDownTimer?=null
    private var countDownEvent: CountDownEvent? = null

    private var handEventAfterDismiss:HandEventAfterDismiss?=null

    private var tv_dialog_game_login_qrcode:ImageView?=null

    init {
        if (null == mcontext) {

        }
        dialog = Dialog(mcontext)
    }

    fun create(): GameLoginDialog {
        if (null == dialog) {
            return this
        }
        contentview = LayoutInflater.from(mcontext).inflate(R.layout.dialog_game_login, null)
        AutoUtils.auto(contentview!!)
        tv_dialog_game_login_qrcode= contentview!!.findViewById(R.id.tv_dialog_game_login_qrcode)



        return this
    }


    fun setMessage(message: String): GameLoginDialog {

        return this
    }



    fun show(): GameLoginDialog {
        if (dialog != null && contentview != null) {
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(contentview!!)
            val params = dialog!!.window!!.attributes
            params.gravity = Gravity.CENTER
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            dialog!!.setCancelable(true)
            dialog!!.setCanceledOnTouchOutside(true)
            dialog!!.setOnDismissListener {
                if (dialog_countDown != null) {
                    dialog_countDown!!.cancel()
                }

                //事件回调
                if (null != handEventAfterDismiss) {
                    handEventAfterDismiss!!.handEvent()
                }
            }
            dialog!!.window!!.attributes = params
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.show()


        }
        return this
    }

    fun dismiss() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }


    //弹窗消失后处理事件
    interface HandEventAfterDismiss {
        fun handEvent()
    }

    //设置弹窗消失后处理事件对象
    fun setHandEventAfterDismiss(handEvent: HandEventAfterDismiss): GameLoginDialog {
        handEventAfterDismiss = handEvent

        return this
    }

    //倒计时处理(有确定按钮)
    interface CountDownEvent {
        fun handCountDownEvent()
    }

    fun setCountDownEvent(c: CountDownEvent): GameLoginDialog {
        countDownEvent = c

        return this
    }


}