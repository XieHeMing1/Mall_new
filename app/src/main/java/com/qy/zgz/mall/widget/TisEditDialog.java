package com.qy.zgz.mall.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by LCB on 2017/11/21 0021.
 */

public class TisEditDialog {

    private Dialog dialog;
    private View contentview;
    private Context mcontext;

    private TextView tv_window_title;

    private EditText et_window_input;

    private Button  btn_tis_yes;

    private Button  btn_tis_no;

    private TextView tv_window_countdown;

    private CountDownTimer dialog_countDown;

    private TextView tv_window_tis_scan_card;

    private ImageView tv_window_tis_scan_card_small;


    public TisEditDialog(Context context)
    {
        mcontext=context;
        dialog=new Dialog(mcontext);
    }

    public TisEditDialog create()
    {
        contentview= LayoutInflater.from(mcontext).inflate(R.layout.window_tis,null);
        AutoUtils.auto(contentview);
        tv_window_title=contentview.findViewById(R.id.tv_window_title);
        btn_tis_yes=contentview.findViewById(R.id.btn_tis_yes);
        btn_tis_no=contentview.findViewById(R.id.btn_tis_no);
        et_window_input=contentview.findViewById(R.id.et_window_input);
        tv_window_countdown=contentview.findViewById(R.id.tv_window_countdown);
        tv_window_tis_scan_card=contentview.findViewById(R.id.tv_window_tis_scan_card);
        tv_window_tis_scan_card_small=contentview.findViewById(R.id.tv_window_tis_scan_card_small);



        return this;
    }


    public TisEditDialog setPositiveButton(final PositiveButtonListener listener)
    {
        btn_tis_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    listener.onClick(v,et_window_input.getText().toString());
                    dismiss();
            }
        });
        return this;
    }


    public TisEditDialog setNegativeButton(final NegativeButtonListener listener)
    {
        btn_tis_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dismiss();
            }
        });
        return this;
    }

    public TisEditDialog setScanCardListener(final ScanCardListener listener)
    {

        btn_tis_yes.setVisibility(View.GONE);
        tv_window_tis_scan_card.setVisibility(View.VISIBLE);
        tv_window_tis_scan_card_small.setVisibility(View.GONE);
        startLoginRecognitionScan(listener);

        return this;
    }


    public TisEditDialog setMessage(String message)
    {
        tv_window_title.setText(message);
        return this;
    }

    public TisEditDialog setEditType(int input_type)
    {
       et_window_input.setInputType(input_type);
        return this;
    }


    public TisEditDialog setPosiButtonVisibility(int visibility)
    {
        btn_tis_yes.setVisibility(visibility);
        return this;
    }

    public TisEditDialog setNegativeButtonVisibility(int visibility)
    {
        btn_tis_no.setVisibility(visibility);
        return this;
    }

    public TisEditDialog show()
    {
        if (dialog!=null && contentview!=null)
        {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(contentview);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.gravity= Gravity.CENTER;
            params.width= WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.setCancelable(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (dialog_countDown!=null){
                        dialog_countDown.cancel();
                    }
                    InputUtils.Companion.closeInput(mcontext);
                    VbarUtils.getInstance((Activity) mcontext).stopScan();
                }
            });
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            dialog_countDown=new CountDownTimer(30000,1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    tv_window_countdown.setText(millisUntilFinished/1000+"秒后自动关闭");
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
        return  this;
    }

    public void dismiss()
    {
        if (dialog!=null && dialog.isShowing())
        {
            dialog.dismiss();
        }

    }

    public interface  NegativeButtonListener
    {
        void onClick(View v);
    }


    public interface  PositiveButtonListener
    {
        void onClick(View v,String input);
    }

    public interface  ScanCardListener
    {
        void scanCard(String scan,String pwd);
    }


    /**
     * 开启出币识别扫描器
     */
    private void startLoginRecognitionScan(ScanCardListener listener){
        try {
            //开启扫描器识别
            VbarUtils.getInstance((Activity) mcontext)
                    .setScanResultExecListener(new VbarUtils.ScanResultExecListener(){
                        @Override
                        public void scanResultExec(String result) {
                            if (!TextUtils.isEmpty(result) && dialog.isShowing()
                                    && !TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info,"").toString())){
                                listener.scanCard(result,et_window_input.getText().toString());
                                dismiss();
                            }
                        }

                    }).getScanResult();

        }catch (Exception e){

        }
    }
}
