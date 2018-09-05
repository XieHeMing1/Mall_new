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
import android.widget.TextView;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.InputUtils;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by LCB on 2017/11/21 0021.
 */

public class TisGameFailureDialog{

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private TextView tv_dialog_window_countdown;

    private Button btn_dialog_game_failure_no;

    private Button btn_dialog_game_failure_yes;

    private AutoLinearLayout all_dialog_game_failure_btn;

    private CountDownTimer dialog_countDown;

//    private LottieAnimationView lv_dialog_failure_animation;


    public TisGameFailureDialog(Context context)
    {
        if (null==context){
            return;
        }
        mcontext=context;
        dialog=new Dialog(mcontext);
    }

    public TisGameFailureDialog create()
    {
        if (null==dialog){
            return this;
        }
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_game_failure,null);
        AutoUtils.auto(contentview);
        btn_dialog_game_failure_yes=contentview.findViewById(R.id.btn_dialog_game_failure_yes);
        btn_dialog_game_failure_no=contentview.findViewById(R.id.btn_dialog_game_failure_no);
        all_dialog_game_failure_btn=contentview.findViewById(R.id.all_dialog_game_failure_btn);
        tv_dialog_window_countdown=contentview.findViewById(R.id.tv_dialog_window_countdown);
//        lv_dialog_failure_animation=contentview.findViewById(R.id.lv_dialog_failure_animation);


        btn_dialog_game_failure_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=positiveButtonListener){
                    positiveButtonListener.onClick(v);
                }
                dismiss();
            }
        });

        btn_dialog_game_failure_no.setOnClickListener(new View.OnClickListener() {
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




    public TisGameFailureDialog setMessage(String message)
    {
        return this;
    }



    public TisGameFailureDialog show()
    {
        if (dialog!=null && contentview!=null)
        {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(contentview);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity= Gravity.CENTER;
            params.width= WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
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
                    tv_dialog_window_countdown.setText(millisUntilFinished/1000+"");

                }
                @Override
                public void onFinish() {
                    btn_dialog_game_failure_no.callOnClick();
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

    public TisGameFailureDialog setNegativeButtonListener(NegativeButtonListener listen){
        negativeButtonListener=listen;

        return this;
    }

    private PositiveButtonListener positiveButtonListener;
    public interface  PositiveButtonListener
    {
        void onClick(View v);
    }

    public TisGameFailureDialog setPositiveButtonListener(PositiveButtonListener listen){
        positiveButtonListener=listen;

        return this;
    }

    private HandEventAfterDismiss handEventAfterDismiss;

    private CountDownEvent countDownEvent;


    //弹窗消失后处理事件
    public interface  HandEventAfterDismiss
    {
        void handEvent();
    }

    //设置弹窗消失后处理事件对象
    public TisGameFailureDialog setHandEventAfterDismiss(HandEventAfterDismiss handEvent){
        handEventAfterDismiss=handEvent;

        return this;
    }

    //倒计时处理(有确定按钮)
    public interface  CountDownEvent
    {
        void handCountDownEvent();
    }

    public TisGameFailureDialog setCountDownEvent(CountDownEvent c){
        countDownEvent=c;

        return this;
    }


}
