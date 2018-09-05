package com.qy.zgz.mall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.dialogfragments.LoginDialogFragment;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index.HomePageActivity;
import com.qy.zgz.mall.page.login.LoginActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.TisDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends RxAppCompatActivity {

    protected Unbinder mUnbinder;

    protected Handler mBaseActivityHandler;

    private LoginDialogFragment mDialog;
    /**
     * 初始化ContentView
     */
    public abstract void createView();

    public abstract void afterCreate(@Nullable Bundle savedInstanceState,
                                     @Nullable Intent intent);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityManager.getActivityManager().addActivity(this);
        MyApplication.getInstance().addActivity(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.i(this.getClass().getSimpleName(), " onCreate invoke");
        createView();
        mUnbinder = ButterKnife.bind(this);
        mBaseActivityHandler = new BaseActivityHandler(this);
        afterCreate(savedInstanceState, getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startLoginRecognitionScan();
    }

    public void logout(Context context) {
//        ActivityManager.getActivityManager().finishAllActivity();
        MyApplication.getInstance().finishActivity();
        SharePerferenceUtil.getInstance().setValue("typeId", "");
        SharePerferenceUtil.getInstance().setValue("cinemaid", "");
        Intent intent = new Intent(context, LoginActivity.class);

        context.startActivity(intent);
    }

    private static class BaseActivityHandler extends Handler {
        private WeakReference<BaseActivity> mActivityWeakReference;

        public BaseActivityHandler(BaseActivity baseActivity) {
            mActivityWeakReference = new WeakReference<BaseActivity>(baseActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity baseActivity = mActivityWeakReference.get();
            if (baseActivity != null && !baseActivity.isFinishing()) {
                baseActivity.handleMessage(msg);
            }
        }
    }

    /**
     * 处理 Handler 发送过来的消息
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {

    }

    public void VipLogout() {
        SharePerferenceUtil.getInstance().setValue(Constance.member_Info, "");
        //清除商城会员登录accessToken
        SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, "");
        //清除商城会员登录shop_id
        SharePerferenceUtil.getInstance().setValue(Constance.shop_id, "");

        ToastUtil.showToast(this, "用户退出");
    }

    public void showLoginDialog(LoginDialogFragment.LoginDialogListener loginDialogListener) {
        mDialog = LoginDialogFragment.newInstance();
        mDialog.setLoginDialogListener(loginDialogListener);
        mDialog.show(getSupportFragmentManager(), LoginDialogFragment.class.getSimpleName());
    }

    public void dismissLoginDialog(){
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
