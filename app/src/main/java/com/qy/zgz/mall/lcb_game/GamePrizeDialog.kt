package com.example.mylock.DynamicLock.Prize

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.qy.zgz.mall.R
import com.zhy.autolayout.utils.AutoUtils

/**
 * Created by LCB on 2018/8/27.
 */

class GamePrizeDialog(private val mcontext: Context?) {

    private var dialog: Dialog?
    private var contentview: View? = null

    private var dialog_countDown:CountDownTimer?=null
    private var countDownEvent: CountDownEvent? = null

    private var handEventAfterDismiss:HandEventAfterDismiss?=null

    private var tv_dialog_game_prize_index:TextView?=null
    private var tv_dialog_game_prize_name:TextView?=null
    private var tv_dialog_game_prize_img:ImageView?=null


    init {
        if (null == mcontext) {

        }
        dialog = Dialog(mcontext)
    }

    fun create(): GamePrizeDialog {
        if (null == dialog) {
            return this
        }
        contentview = LayoutInflater.from(mcontext).inflate(R.layout.dialog_game_prize, null)
        AutoUtils.auto(contentview!!)
        tv_dialog_game_prize_index= contentview!!.findViewById(R.id.tv_dialog_game_prize_index)
        tv_dialog_game_prize_name= contentview!!.findViewById(R.id.tv_dialog_game_prize_name)
        tv_dialog_game_prize_img= contentview!!.findViewById(R.id.tv_dialog_game_prize_img)


        return this
    }


    fun setMessage(message: String): GamePrizeDialog {
        tv_dialog_game_prize_index!!.text=message
        return this
    }



    fun show(): GamePrizeDialog {
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

            dialog_countDown=object :CountDownTimer(6000,1000){
                override fun onFinish() {
                    dismiss()
                }

                override fun onTick(millisUntilFinished: Long) {
                }

            }


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
    fun setHandEventAfterDismiss(handEvent: HandEventAfterDismiss): GamePrizeDialog {
        handEventAfterDismiss = handEvent

        return this
    }

    //倒计时处理
    interface CountDownEvent {
        fun handCountDownEvent()
    }

    fun setCountDownEvent(c: CountDownEvent): GamePrizeDialog {
        countDownEvent = c

        return this
    }


}