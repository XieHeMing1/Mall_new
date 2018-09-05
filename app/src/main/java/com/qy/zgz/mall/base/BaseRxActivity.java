package com.qy.zgz.mall.base;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.MyApplication;
import com.trello.rxlifecycle.components.RxActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by LCB on 2018/3/24.
 *
 * 底层的activity封装
 */


public abstract class BaseRxActivity extends RxActivity{

    private KProgressHUD loadingDialog;
    public Handler handler=new Handler();
    private final int TIMEOUT=300*1000;
    public boolean isRun=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bugFix();
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        MyApplication.getInstance().addActivity(this);
        initDialog();
        ButterKnife.bind(this);
//        handler.postDelayed(exitRunnable, TIMEOUT);
        initView();
    }

    protected abstract int getLayoutId();
    protected abstract void initView();

    private void initDialog()
    {
        loadingDialog = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...");

    }

    private Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            BaseRxActivity.this.finish();
        }
    };

    public void restartExit()
    {
        handler.removeCallbacks(exitRunnable);
        handler.postDelayed(exitRunnable,TIMEOUT);
    }

    public void closeExit()
    {
        handler.removeCallbacks(exitRunnable);
    }

    public void showProgressDialog(String msg) {
        loadingDialog.setLabel(msg).show();
    }

    public void dismissProgressDialog() {
        loadingDialog.dismiss();
    }

    private void bugFix() {
        String brand = Build.BRAND;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (brand != null && brand.equals("OPPO")) {
                oppoActionBar();
            }
            if (brand != null && brand.equals("Xiaomi")){
                xiaomiUi();
            }
        }
        /**6.0*/
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            android6systemui();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void oppoActionBar() {
        getWindow().addFlags(0x80000000);
        getWindow().setStatusBarColor(0x0);
        getWindow().getDecorView().setSystemUiVisibility(0x10);
    }

    private void xiaomiUi() {
        /*** 设置Android状态栏的字体颜色，状态栏为亮色的时候字体和图标是黑色，状态栏为暗色的时候字体和图标为白色*/
        try {
            Window window = getWindow();
            Class clazz = getWindow().getClass();
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (true) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {       //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // android6.0+系统隐藏状态栏阴影
    private void android6systemui() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(this);
        isRun=false;
        loadingDialog.dismiss();
    }
}
