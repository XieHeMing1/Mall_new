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

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by LCB on 2017/11/21 0021.
 */

public class TisGameSuccessDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private Button btn_dialog_game_success_no;

    private Button btn_dialog_game_success_yes;

    private AutoRelativeLayout all_dialog_game_success_btn;

    private CountDownTimer dialog_countDown;

    private ImageView iv_dialog_tis_game_success_qrcode;

    private SimpleDraweeView dialog_tis_game_success_awardimg;

//    private CircleTextProgressbar ctp_dialog_game_success_countdown;


    public TisGameSuccessDialog(Context context)
    {
        if (null==context){
            return;
        }
        mcontext=context;
        dialog=new Dialog(mcontext);
    }

    public TisGameSuccessDialog create()
    {
        if (null==dialog){
            return this;
        }
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.dialog_game_success,null);
        AutoUtils.auto(contentview);
        btn_dialog_game_success_yes=contentview.findViewById(R.id.btn_dialog_game_success_yes);
        btn_dialog_game_success_no=contentview.findViewById(R.id.btn_dialog_game_success_no);
        all_dialog_game_success_btn=contentview.findViewById(R.id.all_dialog_game_success_btn);
        iv_dialog_tis_game_success_qrcode=contentview.findViewById(R.id.dialog_tis_game_success_qrcode);
        dialog_tis_game_success_awardimg=contentview.findViewById(R.id.dialog_tis_game_success_awardimg);
//        ctp_dialog_game_success_countdown=contentview.findViewById(R.id.ctp_dialog_game_success_countdown);
//        ctp_dialog_game_success_countdown.setTimeMillis(20000);
//        ctp_dialog_game_success_countdown.setOutLineColor(Color.parseColor("#FFFFFF"));
//        ctp_dialog_game_success_countdown.setProgressType(CircleTextProgressbar.ProgressType.COUNT_BACK);
//        ctp_dialog_game_success_countdown.setCountdownProgressListener(0, new CircleTextProgressbar.OnCountdownProgressListener() {
//            @Override
//            public void onProgress(int what, int progress,long time) {
//                ctp_dialog_game_success_countdown.setText(time+"");
//                if (progress==0){
//                    btn_dialog_game_success_no.callOnClick();
//                }
//            }
//        });

        btn_dialog_game_success_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=positiveButtonListener){
                    positiveButtonListener.onClick(v);
                }
                if (dialog!=null){
                    dialog.setOnDismissListener(null);
                }
                dismiss();
            }
        });

        btn_dialog_game_success_no.setOnClickListener(new View.OnClickListener() {
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




    public TisGameSuccessDialog setQrURL(String url)
    {
        try {
            iv_dialog_tis_game_success_qrcode.setImageBitmap(QRBitmapUtils.createQRCode(url,300));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public TisGameSuccessDialog setAwardImg(String url)
    {
        try {
            dialog_tis_game_success_awardimg.setImageURI(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }





    public TisGameSuccessDialog show()
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
//                    ctp_dialog_game_success_countdown.stop();
                    //事件回调
                    if (null!=handEventAfterDismiss){
                        handEventAfterDismiss.handEvent();
                    }
                }
            });
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
//            ctp_dialog_game_success_countdown.start();


            dialog_countDown= new CountDownTimer(18000,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }
                @Override
                public void onFinish() {
                    btn_dialog_game_success_no.callOnClick();
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

    public TisGameSuccessDialog setNegativeButtonListener(NegativeButtonListener listen){
        negativeButtonListener=listen;

        return this;
    }

    private PositiveButtonListener positiveButtonListener;
    public interface  PositiveButtonListener
    {
        void onClick(View v);
    }

    public TisGameSuccessDialog setPositiveButtonListener(PositiveButtonListener listen){
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
    public TisGameSuccessDialog setHandEventAfterDismiss(HandEventAfterDismiss handEvent){
        handEventAfterDismiss=handEvent;

        return this;
    }

    //倒计时处理(有确定按钮)
    public interface  CountDownEvent
    {
        void handCountDownEvent();
    }

    public TisGameSuccessDialog setCountDownEvent(CountDownEvent c){
        countDownEvent=c;

        return this;
    }


}
