package com.qy.zgz.mall.page.index_function;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.widget.MyWebView;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * Created by LCB on 2018/2/4.
 */

public class CustomerServiceDialog implements View.OnTouchListener{

        private Dialog dialog;
        private View contentview;
        private Context mcontext;
        private MyWebView wb_customer_service_info;
        private ImageView iv_dialog_customer_service_close;

        private CountDownTimer countDownTimer=new CountDownTimer(30000,1000) {
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
        };

        public CustomerServiceDialog(Context context)
        {
            mcontext=context;
            dialog=new Dialog(mcontext,R.style.dialogstyle);

        }

        public CustomerServiceDialog create()
        {
            contentview= LayoutInflater.from(mcontext).inflate(R.layout.customer_service,null);
            AutoUtils.auto(contentview);
            wb_customer_service_info=contentview.findViewById(R.id.wb_customer_service_info);
            iv_dialog_customer_service_close=contentview.findViewById(R.id.iv_dialog_customer_service_close);

            iv_dialog_customer_service_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            contentview.setOnTouchListener(this);

            initWeb();

            return this;
        }

        public CustomerServiceDialog show()
        {
            if (dialog!=null && contentview!=null)
            {
                dialog.setContentView(contentview);
                WindowManager.LayoutParams params=dialog.getWindow().getAttributes();
                params.gravity= Gravity.CENTER;
//                params.width=AutoUtils.getPercentWidthSize(1800);
                params.width=WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(params);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //关闭键盘
                        InputUtils.Companion.closeInput(mcontext);
                        countDownTimer.cancel();
                    }
                });
                dialog.show();

                countDownTimer.start();

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

        public Boolean isShowing()
        {
            return dialog.isShowing();
        }

    private void initWeb()
    {
        wb_customer_service_info.setOnTouchScreenListener(new MyWebView.OnTouchScreenListener(){

            @Override
            public void onTouchScreen() {
                //按下
                countDownTimer.cancel();
                countDownTimer.start();
            }

            @Override
            public void onReleaseScreen() {
                //抬起
                countDownTimer.cancel();
                countDownTimer.start();
            }
        });
        //支持App内部javascript交互
        wb_customer_service_info.getSettings().setJavaScriptEnabled(true);

        wb_customer_service_info.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //自适应屏幕
        wb_customer_service_info.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wb_customer_service_info.getSettings().setLoadWithOverviewMode(true);
        //设置可以支持缩放
        wb_customer_service_info.getSettings().setSupportZoom(true);
        //扩大比例的缩放
        wb_customer_service_info.getSettings().setUseWideViewPort(true);
        //设置是否出现缩放工具
        wb_customer_service_info.getSettings().setBuiltInZoomControls(true);
        //不使用Android默认浏览器打开Web，就在App内部打开Web
        wb_customer_service_info.setWebViewClient(new WebViewClient() {
            @Override
             public boolean shouldOverrideUrlLoading( WebView view,  String url)  {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                countDownTimer.cancel();
                countDownTimer.start();
                super.onLoadResource(view, url);
            }

        });

        wb_customer_service_info.loadUrl("http://hz-v7.ntalker.com/downt/t2d/chat.php?v=2018.03.06&siteid=kf_10169&settingid=kf_10169_1521513586764&baseuri=http%3A%2F%2Fdl.ntalker.com%2Fjs%2Fxn6%2F&mobile=1&ref=http%3A%2F%2Fwww.hyppmm.com%2Fwap%2Fshop-index.html%3Fshop_id%3D4&iframechat=0&header=1&rnd=1522490359713");
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        countDownTimer.cancel();
        countDownTimer.start();
        return false;
    }
}
