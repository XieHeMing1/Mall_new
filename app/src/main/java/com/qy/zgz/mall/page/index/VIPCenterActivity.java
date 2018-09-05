package com.qy.zgz.mall.page.index;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseActivity;
import com.qy.zgz.mall.Model.Cranemaapi;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.Model.MyOrder;
import com.qy.zgz.mall.Model.PurchaseRecord;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.TabPagerAdapter;
import com.qy.zgz.mall.lcb_game.NumDanceActivity;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.fragment.AllOrderFragment;
import com.qy.zgz.mall.page.fragment.DeliveryFragment;
import com.qy.zgz.mall.page.fragment.EvalutionFragment;
import com.qy.zgz.mall.page.fragment.MemberCenterFragment;
import com.qy.zgz.mall.page.fragment.PaymentFragment;
import com.qy.zgz.mall.page.fragment.ReceiverFragment;
import com.qy.zgz.mall.page.fragment.VipCenterFragment;
import com.qy.zgz.mall.page.fragment.VipCenterInfoFragment;
import com.qy.zgz.mall.page.index_function.CustomerServiceDialog;
import com.qy.zgz.mall.slot_machines.game.SlotMachinesActivity;

import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.TimeUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.MyTextView;
import com.qy.zgz.mall.widget.TisDialog;
import com.youth.banner.Banner;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 */
public class VIPCenterActivity extends BaseActivity {
    private String mCinemaType;
    private String mCinemaid;

    @BindView(R.id.ban_main_banner)
    Banner mBanner;
    private Cranemaapi mCranemaApi;

    /* 底部信息栏 */
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    @BindView(R.id.tv_login_please)
    TextView mTvLoginPlease;
    @BindView(R.id.tv_vip_name)
    MyTextView mTvVipName;
    @BindView(R.id.tv_lottery)
    MyTextView mTvlottery;
    @BindView(R.id.tv_lottery_count)
    MyTextView mTvLotteryCount;
    @BindView(R.id.tv_game_coin)
    MyTextView mTvGameCoin;
    @BindView(R.id.tv_game_coin_count)
    MyTextView mTvGameCoinCount;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.iv_shopping_cart)
    ImageView mIvShoppingCart;
    @BindView(R.id.fl_fragment_container)
    FrameLayout mFlContainer;
    @BindView(R.id.ll_vip_info_layout)
    AutoLinearLayout mLlVipInfoLayout;

    private static String TAG = "VIPCenterActivity";

    private LoginCountDownTimer countDownTimer;

    @Override
    public void createView() {
        setContentView(R.layout.activity_vip_center);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState, @Nullable Intent intent) {
        mCinemaType = (String) SharePerferenceUtil.getInstance().getValue("typeId", "");
        mCinemaid = (String) SharePerferenceUtil.getInstance().getValue("cinemaid", "");
//        mSdvCustomorEnquiry.setImageURI(LocalDefines.getImgUriHead(this) + R.drawable.ic_customor_enquiry);
//        mSdvSetting.setImageURI(LocalDefines.getImgUriHead(this) + R.drawable.ic_setting);
        countDownTimer = new LoginCountDownTimer(this, 30000, 1000);
        mTvShopName.setText(SharePerferenceUtil.getInstance().getValue("type_shop_name","").toString());
        initBanner();
//        viewLongClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoginInfo();
//        consumptionOrderList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止扫描
        VbarUtils.getInstance(this).stopScan();
        countDownTimer.cancel();
        mBaseActivityHandler.removeCallbacksAndMessages(null);
    }

    //用户操作监听
    @Override
    public void onUserInteraction() {
//        Log.i("test_xhm", "onUserInteraction");
        Constance.lastTouchTime = System.currentTimeMillis();
        //重新倒计时
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            countDownTimer.cancel();
        } else {
            countDownTimer.cancel();
            countDownTimer.start();
        }

        super.onUserInteraction();
    }

//    private void viewLongClickListener(){
//        mIvSetting.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                new UnityDialog(VIPCenterActivity.this).setHint("是否更换店铺")
//                        .setCancel("取消", null)
//                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
//                            @Override
//                            public void confirm(UnityDialog unityDialog, String content) {
//                                SharePerferenceUtil.getInstance().setValue("typeId", "");
//                                SharePerferenceUtil.getInstance().setValue("cinemaid", "");
//                                SharePerferenceUtil.getInstance().setValue("type_shop_name", "");
//                                //清除机器场地BranchID
//                                SharePerferenceUtil.getInstance().setValue(Constance.BranchID,"");
//                                //清除机器ID
//                                SharePerferenceUtil.getInstance().setValue(Constance.MachineID,"");
//                                //清除机器VPN
//                                SharePerferenceUtil.getInstance().setValue(Constance.Vpn,"");
//                                //清除会员登录信息
//                                SharePerferenceUtil.getInstance().setValue(Constance.member_Info,"");
//                                //清除商城会员登录accessToken
//                                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken,"");
//                                //清除商城会员登录shop_id
//                                SharePerferenceUtil.getInstance().setValue(Constance.shop_id,"");
//                                MyApplication.getInstance().restartApp();
//                            }
//                        });
//                return false;
//            }
//        });
//    }

    private void initBanner() {
        ArrayList<Integer> banList = new ArrayList<>();
        banList.add(R.drawable.banner);
        //banner设置
        mBanner.setDelayTime(10000);//设置轮播时间
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setImages(banList).start();

        long time = 0L;
        long lastGetBannerTime = (long)SharePerferenceUtil.getInstance().getValue(LocalDefines.LAST_GET_BANNER_TIME, time);
        Log.i(TAG, "time lastTime = " + lastGetBannerTime + " System.currentTimeMillis() = " + System.currentTimeMillis() + " System.currentTimeMillis() - lastGetBannerTime = " + (System.currentTimeMillis() - lastGetBannerTime)) ;

        String json =  SharePerferenceUtil.getInstance().getValue("cranemaapi", "").toString();
        Log.i(TAG, "local json = " + json);
        Gson gson = new Gson();
        if (json != null && !json.equals("")) {
            mCranemaApi = gson.fromJson(json, Cranemaapi.class);
            if (null != mCranemaApi.getImages() && !mCranemaApi.getImages().isEmpty() || System.currentTimeMillis() - lastGetBannerTime < 43200000) {
//                Log.i(TAG, "mCranemaApi.getVideodata() = " + mCranemaApi.getVideodata().get(0).getImages());
                if (mCranemaApi.getImages() != null) {
                    Log.i(TAG, "local has Images data");
//                    Log.i(TAG, "mCranemaApi.getVideodata().get(0).getImages() != null = " + mCranemaApi.getVideodata().get(0).getImages());
                    mBanner.update(mCranemaApi.getImages());
                    return;
                }else {
                    Log.i(TAG, "local has not Images data");
                    getCranemaFromServer();
                }
            }
        }else {
            Log.i(TAG, "local has not data");
            getCranemaFromServer();
        }
    }

    private void getCranemaFromServer() {
        NetworkRequest.getInstance().getCranemaapi(mCinemaType, mCinemaid, new NetworkCallback<Cranemaapi>() {
            @Override
            public void onSuccess(Cranemaapi data) {
                mCranemaApi = data;
                Gson gson = new Gson();
                String json = gson.toJson(data);
                SharePerferenceUtil.getInstance().setValue("cranemaapi", json);
                SharePerferenceUtil.getInstance().setValue(LocalDefines.LAST_GET_BANNER_TIME, System.currentTimeMillis());
                if (TimeUtil.getInstance().isExceedTime()) {
                    FileManager.getInstance().deleteSvaeFile();
                    FileManager.getInstance().deleteAPKSvaeFile();
                }
                if (null != mCranemaApi.getImages() && !mCranemaApi.getVideodata().isEmpty()) {
                    mBanner.update(mCranemaApi.getImages());
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                ToastUtil.showToast(VIPCenterActivity.this, msg);
            }

            @Override
            public void onCompleted() {
//                showData();
                super.onCompleted();
                if (null != mCranemaApi.getImages() && !mCranemaApi.getImages().isEmpty()) {
                    mBanner.update(mCranemaApi.getImages());
                }
            }
        });
    }

    @OnClick({R.id.btn_logout, R.id.iv_new_game, R.id.iv_vip_center,
            R.id.iv_purchase_coin, R.id.iv_exchange_mall, R.id.iv_lucky_lottery,
            R.id.sdv_customor_enquiry, R.id.tv_main_page})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_logout:
//                logout(this);
                VipLogout();
                showLoginInfo();
                break;
            case R.id.iv_new_game:
                intent.setClass(this, NumDanceActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_vip_center:
                ToastUtil.showToast(this, "已达该页");
                break;
            case R.id.iv_purchase_coin:
                intent.setClass(this, PurchaseCoinActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_exchange_mall:
                intent.setClass(this, MallActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_lucky_lottery:
                intent.setClass(this, SlotMachinesActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.sdv_customor_enquiry:
                new CustomerServiceDialog(this).create().show();
                break;
            case R.id.tv_main_page:
                intent.setClass(VIPCenterActivity.this, HomePageActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    //显示或隐藏登录(登录了机台信息)
    public void showLoginInfo() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            LocalDefines.sIsLogin = false;
            countDownTimer.cancel();
            //未登录状态
            replaceFragment(new VipCenterFragment());
            mFlContainer.setVisibility(View.VISIBLE);
            mLlVipInfoLayout.setVisibility(View.GONE);
            mTvLoginPlease.setVisibility(View.VISIBLE);
            mTvVipName.setVisibility(View.INVISIBLE);
            mTvlottery.setVisibility(View.INVISIBLE);
            mTvLotteryCount.setVisibility(View.INVISIBLE);
            mTvGameCoin.setVisibility(View.INVISIBLE);
            mTvGameCoinCount.setVisibility(View.GONE);
            mBtnLogout.setVisibility(View.GONE);
//            mIvShoppingCart.setVisibility(View.GONE);

        } else {
            LocalDefines.sIsLogin = true;
            countDownTimer.cancel();
            countDownTimer.start();
            //初始化登录信息
            String logininfo = SharePerferenceUtil.getInstance()
                    .getValue(Constance.member_Info, "").toString();
            MemberInfo loginJson = GsonUtil.Companion.jsonToObject(logininfo, MemberInfo.class);

            if (loginJson != null) {
                replaceFragment(new VipCenterInfoFragment());
                mFlContainer.setVisibility(View.VISIBLE);
                mTvLoginPlease.setVisibility(View.GONE);
                mTvVipName.setVisibility(View.VISIBLE);
                mTvlottery.setVisibility(View.VISIBLE);
//                mTvLotteryCount.setVisibility(View.VISIBLE);
                mTvGameCoin.setVisibility(View.VISIBLE);
                mTvGameCoinCount.setVisibility(View.VISIBLE);
                mBtnLogout.setVisibility(View.VISIBLE);
                mBtnLogout.setBackgroundResource(R.drawable.shape_logout);
                mBtnLogout.setClickable(true);
                mIvShoppingCart.setVisibility(View.VISIBLE);
                mTvLotteryCount.setText(loginJson.getTickets().substring(0, loginJson.getTickets().indexOf(".")));
                mTvGameCoinCount.setText(loginJson.getCoins().substring(0, loginJson.getCoins().indexOf(".")));
                mTvVipName.setText(loginJson.getCustName());
            }
        }
    }

    // 登录计时器
    static class LoginCountDownTimer extends CountDownTimer {
        WeakReference<VIPCenterActivity> mWeakReference;

        public LoginCountDownTimer(VIPCenterActivity vipCenterActivity, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            mWeakReference = new WeakReference<>(vipCenterActivity);
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            VIPCenterActivity vipCenterActivity = mWeakReference.get();
            if (vipCenterActivity != null) {
                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info, "");
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken, "");
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id, "");

                //重新初始化
                vipCenterActivity.onResume();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) { // 计时过程显示
            VIPCenterActivity vipCenterActivity = mWeakReference.get();
            if (vipCenterActivity != null) {

            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        transaction.replace(R.id.fl_fragment_container, fragment);
        transaction.commit();
    }

}
