package com.qy.zgz.mall.widget;

import android.app.Activity;
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
 * Created by ZYB on 2017/11/21 0021.
 */

public class TisDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private TextView tv_dialog_window_info;

    private TextView tv_dialog_window_countdown;

    private Button btn_dialog_window_tis_no;

    private Button btn_dialog_window_tis_yes;

    private AutoLinearLayout all_dialog_window_tis_btn;

    private CountDownTimer dialog_countDown;

    public TisDialog(Context context)
    {
        if (null==context){
            return;
        }
        mcontext=context;
        dialog=new Dialog(mcontext);
    }

    public TisDialog create()
    {
        if (null==dialog){
            return this;
        }
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_window_tis,null);
        AutoUtils.auto(contentview);
        tv_dialog_window_info=contentview.findViewById(R.id.tv_dialog_window_info);
        btn_dialog_window_tis_yes=contentview.findViewById(R.id.btn_dialog_window_tis_yes);
        btn_dialog_window_tis_no=contentview.findViewById(R.id.btn_dialog_window_tis_no);
        all_dialog_window_tis_btn=contentview.findViewById(R.id.all_dialog_window_tis_btn);
        tv_dialog_window_countdown=contentview.findViewById(R.id.tv_dialog_window_countdown);

        btn_dialog_window_tis_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=positiveButtonListener){
                    positiveButtonListener.onClick(v);
                }
                dismiss();
            }
        });

        btn_dialog_window_tis_no.setOnClickListener(new View.OnClickListener() {
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




    public TisDialog setMessage(String message)
    {
        tv_dialog_window_info.setText(message);
        return this;
    }

    public TisDialog setBtnShow()
    {
        all_dialog_window_tis_btn.setVisibility(View.VISIBLE);
        return this;
    }




    public TisDialog show()
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

            if (all_dialog_window_tis_btn.isShown()){
                dialog_countDown= new CountDownTimer(20000,1000){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_dialog_window_countdown.setText(millisUntilFinished/1000+"秒后自动取消");
                        try {
                            ((Activity)mcontext).onUserInteraction();
                        }catch (Exception e){

                        }

                        //事件回调
                        if (null!=countDownEvent){
                            countDownEvent.handCountDownEvent();
                        }

                    }

                    @Override
                    public void onFinish() {
                        if (null!=negativeButtonListener){
                            btn_dialog_window_tis_no.callOnClick();
                        }
                        dismiss();
                    }
                }.start();
            }else{

            dialog_countDown= new CountDownTimer(2000,1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    try {
                        ((Activity)mcontext).onUserInteraction();
                    }catch (Exception e){

                    }
                }

                @Override
                public void onFinish() {
                    dismiss();
                }
            }.start();

            }

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

    public TisDialog setNegativeButtonListener(NegativeButtonListener listen){
        negativeButtonListener=listen;

        return this;
    }

    private PositiveButtonListener positiveButtonListener;
    public interface  PositiveButtonListener
    {
        void onClick(View v);
    }

    public TisDialog setPositiveButtonListener(PositiveButtonListener listen){
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
    public TisDialog setHandEventAfterDismiss(HandEventAfterDismiss handEvent){
        handEventAfterDismiss=handEvent;

        return this;
    }

    //倒计时处理(有确定按钮)
    public interface  CountDownEvent
    {
        void handCountDownEvent();
    }

    public  TisDialog setCountDownEvent(CountDownEvent c){
        countDownEvent=c;

        return this;
    }


}
