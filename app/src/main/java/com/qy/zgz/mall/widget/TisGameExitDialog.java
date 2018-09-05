package com.qy.zgz.mall.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.InputUtils;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by LCB on 2017/11/21 0021.
 */

public class TisGameExitDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private Button btn_dialog_game_exit_no;

    private Button btn_dialog_game_exit_yes;

    private AutoLinearLayout all_dialog_game_exit_btn;

    private CountDownTimer dialog_countDown;

    private ImageView iv_dialog_game_exit_log;

    public TisGameExitDialog(Context context)
    {
        if (null==context){
            return;
        }
        mcontext=context;
        dialog=new Dialog(mcontext);
    }

    public TisGameExitDialog create()
    {
        if (null==dialog){
            return this;
        }
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_game_exit,null);
        AutoUtils.auto(contentview);
        btn_dialog_game_exit_yes=contentview.findViewById(R.id.btn_dialog_game_exit_yes);
        btn_dialog_game_exit_no=contentview.findViewById(R.id.btn_dialog_game_exit_no);
        all_dialog_game_exit_btn=contentview.findViewById(R.id.all_dialog_game_exit_btn);
        iv_dialog_game_exit_log=contentview.findViewById(R.id.iv_dialog_game_exit_log);


        iv_dialog_game_exit_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //事件回调
                if (null!=cancelClickListener){
                    cancelClickListener.handEvent();
                }
                dismiss();
            }
        });

        btn_dialog_game_exit_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=positiveButtonListener){
                    positiveButtonListener.onClick(v);
                }
                dismiss();
            }
        });

        btn_dialog_game_exit_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=negativeButtonListener){
                    negativeButtonListener.onClick(v);
                }
                dismiss();

            }
        });

        return this;
    }




    public TisGameExitDialog setMessage(String message)
    {
        return this;
    }



    public TisGameExitDialog show()
    {
        if (dialog!=null && contentview!=null)
        {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(contentview);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity= Gravity.CENTER;
            params.width= WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (dialog_countDown!=null){
                        dialog_countDown.cancel();
                    }
                    InputUtils.Companion.closeInput(mcontext);

                    //事件回调
                    if (null!=handEventAfterDismiss){
                        handEventAfterDismiss.handEvent();
                    }
                }
            });
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();


            dialog_countDown= new CountDownTimer(10000,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }
                @Override
                public void onFinish() {
                    iv_dialog_game_exit_log.callOnClick();
                }
            }.start();

        }

        return  this;
    }

    public void dismiss()
    {
        if (dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }
    }

    private NegativeButtonListener negativeButtonListener;

    public interface  NegativeButtonListener
    {
        void onClick(View v);
    }

    public TisGameExitDialog setNegativeButtonListener(NegativeButtonListener listen){
        negativeButtonListener=listen;

        return this;
    }

    private PositiveButtonListener positiveButtonListener;
    public interface  PositiveButtonListener
    {
        void onClick(View v);
    }

    public TisGameExitDialog setPositiveButtonListener(PositiveButtonListener listen){
        positiveButtonListener=listen;

        return this;
    }

    private HandEventAfterDismiss handEventAfterDismiss;

    private CountDownEvent countDownEvent;

    private CancelClickListener cancelClickListener;


    //弹窗消失后处理事件
    public interface  HandEventAfterDismiss
    {
        void handEvent();
    }

    //设置弹窗消失后处理事件对象
    public TisGameExitDialog setHandEventAfterDismiss(HandEventAfterDismiss handEvent){
        handEventAfterDismiss=handEvent;

        return this;
    }

    //倒计时处理(有确定按钮)
    public interface  CountDownEvent
    {
        void handCountDownEvent();
    }

    public TisGameExitDialog setCountDownEvent(CountDownEvent c){
        countDownEvent=c;

        return this;
    }

    //弹窗消失后处理事件
    public interface  CancelClickListener
    {
        void handEvent();
    }

    //设置弹窗消失后处理事件对象
    public TisGameExitDialog setCancelClickListener(CancelClickListener ccl){
        cancelClickListener=ccl;

        return this;
    }

}
