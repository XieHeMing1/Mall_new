package com.qy.zgz.mall.page.index;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseActivity;
import com.qy.zgz.mall.KDSerialPort.KDSerialPort.kd;
import com.qy.zgz.mall.Model.Cranemaapi;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.Model.PurchaseRecord;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.ConsumptionListAdapter;
import com.qy.zgz.mall.dialogfragments.LoginDialogFragment;
import com.qy.zgz.mall.lcb_game.NumDanceActivity;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.fragment.ConsumptionRecordFragment;
import com.qy.zgz.mall.page.fragment.ManagerSystemFragment;
import com.qy.zgz.mall.page.fragment.MemberCenterFragment;
import com.qy.zgz.mall.page.fragment.ModifyPwdFragment;
import com.qy.zgz.mall.page.fragment.PurchaseAndTakeCoinFragment;
import com.qy.zgz.mall.page.fragment.ShopCarFragment;
import com.qy.zgz.mall.page.fragment.ShoppingCartBottomFragment;
import com.qy.zgz.mall.page.index_function.CustomerServiceDialog;
import com.qy.zgz.mall.slot_machines.game.SlotMachinesActivity;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.QRBitmapUtils;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.TimeUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.MyTextView;
import com.qy.zgz.mall.widget.TisDialog;
import com.qy.zgz.mall.widget.TisEditDialog;
import com.youth.banner.Banner;
import com.zhy.autolayout.AutoLinearLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 */
public class PurchaseCoinActivity extends BaseActivity {
    public static final String TAG = "PurchaseCoinActivity";
    @BindView(R.id.ban_main_banner)
    Banner mBanner;

    @BindView(R.id.iv_wx_qrcode)
    ImageView mIvWxQRCode;
    @BindView(R.id.rl_purchase_coin)
    ImageView mRlPurchaseCoin;
    @BindView(R.id.rl_exchange_coin)
    ImageView mRlExchangeCoin;
    @BindView(R.id.rl_consumption_records)
    ImageView mRlConSumptionRecords;
    @BindView(R.id.rl_modify_password)
    ImageView mRlModifyPwd;
    @BindView(R.id.iv_logout)
    ImageView mIvLogout;
    @BindView(R.id.txt_timer_count)
    TextView mTvTimerCount;
    @BindView(R.id.sdv_customor_enquiry)
    ImageView mSdvCustomer;
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    ;

    /* 底部信息栏 */
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


    @BindView(R.id.tv_purchase_coin_type)
    TextView mTvPurchaseTitle;
    @BindView(R.id.v_buy_coins_clean_error)
    View mViewCleanError;

//    /**
//     * 修改密码布局
//     */
//    @BindView(R.id.ll_modify_pwd_layout)
//    LinearLayout mLlModifyPwd;
//    @BindView(R.id.et_pwdupdate_oldpwd)
//    EditText mEtOldPwd;
//    @BindView(R.id.et_pwdupdate_npwd)
//    EditText mEtNewPwd;
//    @BindView(R.id.et_pwdupdate_cpwd)
//    EditText mEtConfirmPwd;
//    @BindView(R.id.btn_pwdupdate_cancel)
//    Button mBtnCancel;
//    @BindView(R.id.btn_pwdupdate_confirm)
//    Button mBtnConfirm;

    @BindView(R.id.iv_high_setting)
    ImageView mIvSetting;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.iv_shopping_cart)
    ImageView mIvShoppingCart;

    //经理卡
    @BindView(R.id.fl_fragment_container)
    FrameLayout mflContaner;
    @BindView(R.id.ll_right_layout)
    AutoLinearLayout mllRightLayout;
    @BindView(R.id.iv_manager_setting)
    ImageView mIvManagerSetting;

    private String mCinemaType;
    private String mCinemaid;
    private Cranemaapi mCranemaApi;
    private ShoppingTimeCount mTimeCount;
    //检查微信登录handle
    private Handler wx_handle = new Handler();
    public static String wx_qrcode = "";

    ConsumptionListAdapter mConSumptionListAdapter;
    List<PurchaseRecord> mConsumptionRecordList;
    private String mConsumptionData = null;

    @Override
    public void createView() {
        setContentView(R.layout.activity_purchase_coin);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState, @Nullable Intent intent) {
        mCinemaType = (String) SharePerferenceUtil.getInstance().getValue("typeId", "");
        mCinemaid = (String) SharePerferenceUtil.getInstance().getValue("cinemaid", "");
//        mSdvSetting.setImageURI(LocalDefines.getImgUriHead(this) + R.drawable.ic_setting);
        mTimeCount = new ShoppingTimeCount(PurchaseCoinActivity.this, 30000, 1000);
        mTimeCount.start();
        mTvShopName.setText(SharePerferenceUtil.getInstance().getValue("type_shop_name", "").toString());
        initBanner();

        Bundle bundle = new Bundle();
        bundle.putInt("type", 1);
        PurchaseAndTakeCoinFragment.newInstance().setArguments(bundle);
        replaceFragment(PurchaseAndTakeCoinFragment.newInstance());

        mViewCleanError.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new UnityDialog(PurchaseCoinActivity.this).setHint("是否清除出币故障？")
                        .setCancel("取消", null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                kd.sp().bdCleanError();
                                kd.sp().bdCoinOuted();
                                kd.sp().setIsSuccessOutCoin(true);
                            }
                        });
                return false;
            }
        });

        viewLongClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoginInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止扫描
        VbarUtils.getInstance(this).stopScan();
        mTimeCount.cancel();
        mBaseActivityHandler.removeCallbacksAndMessages(null);
        wx_handle.removeCallbacksAndMessages(null);
    }

    //用户操作监听
    @Override
    public void onUserInteraction() {
        Constance.lastTouchTime = System.currentTimeMillis();
        //重新倒计时
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            mTimeCount.cancel();
        } else {
            mTimeCount.cancel();
            mTimeCount.start();
        }

        super.onUserInteraction();
    }

    private void initBanner() {
        ArrayList<Integer> banList = new ArrayList<>();
        banList.add(R.drawable.banner);
        //banner设置
        mBanner.setDelayTime(10000);//设置轮播时间
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setImages(banList).start();

        long time = 0L;
        long lastGetBannerTime = (long) SharePerferenceUtil.getInstance().getValue(LocalDefines.LAST_GET_BANNER_TIME, time);
        Log.i(TAG, "time lastTime = " + lastGetBannerTime + " System.currentTimeMillis() = " + System.currentTimeMillis() + " System.currentTimeMillis() - lastGetBannerTime = " + (System.currentTimeMillis() - lastGetBannerTime));

        String json = SharePerferenceUtil.getInstance().getValue("cranemaapi", "").toString();
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
                } else {
                    Log.i(TAG, "local has not Images data");
                    getCranemaFromServer();
                }
            }
        } else {
//            Log.i(TAG, "local has not data");
            getCranemaFromServer();
        }
    }

    private void viewLongClickListener() {
        mIvSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new UnityDialog(PurchaseCoinActivity.this).setHint("是否更换店铺")
                        .setCancel("取消", null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                SharePerferenceUtil.getInstance().setValue("typeId", "");
                                SharePerferenceUtil.getInstance().setValue("cinemaid", "");
                                SharePerferenceUtil.getInstance().setValue("type_shop_name", "");
                                //清除机器场地BranchID
                                SharePerferenceUtil.getInstance().setValue(Constance.BranchID, "");
                                //清除机器ID
                                SharePerferenceUtil.getInstance().setValue(Constance.MachineID, "");
                                //清除机器VPN
                                SharePerferenceUtil.getInstance().setValue(Constance.Vpn, "");
                                //清除会员登录信息
                                SharePerferenceUtil.getInstance().setValue(Constance.member_Info, "");
                                //清除商城会员登录accessToken
                                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, "");
                                //清除商城会员登录shop_id
                                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, "");
                                MyApplication.getInstance().restartApp();
                            }
                        });
                return false;
            }
        });
    }

    private void getCranemaFromServer() {
        Log.i(TAG, "getCranemaFromServer = " + mCinemaType + " mCinemaid = " + mCinemaid);
        NetworkRequest.getInstance().getCranemaapi(mCinemaType, mCinemaid, new NetworkCallback<Cranemaapi>() {
            @Override
            public void onSuccess(Cranemaapi data) {
                Log.i(TAG, "getCranemaFromServer data = " + data);
                mCranemaApi = data;
                Gson gson = new Gson();
                String json = gson.toJson(data);
                Log.i(TAG, "getCranemaFromServer json = " + json);
                SharePerferenceUtil.getInstance().setValue("cranemaapi", json);
                SharePerferenceUtil.getInstance().setValue(LocalDefines.LAST_GET_BANNER_TIME, System.currentTimeMillis());
//                initDot();
//                showData();
                if (TimeUtil.getInstance().isExceedTime()) {
                    FileManager.getInstance().deleteSvaeFile();
                    FileManager.getInstance().deleteAPKSvaeFile();
                }
                //下载图片
//                getImageList();
//                handler.removeCallbacks(getCranRunnable);
                if (null != mCranemaApi.getImages() && !mCranemaApi.getImages().isEmpty()) {
                    if (mCranemaApi.getImages() != null) {
                        mBanner.update(mCranemaApi.getImages());
                    } else {
                        Log.i(TAG, "服务器无VideoData返回");
                    }
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i(TAG, "getCranemaFromServer onFailure");
                ToastUtil.showToast(PurchaseCoinActivity.this, msg);
//                handler.postDelayed(getCranRunnable, 5000);
//                showData();
            }

            @Override
            public void onCompleted() {
//                showData();
                super.onCompleted();
                Log.i(TAG, "getCranemaFromServer onCompleted");
            }
        });
    }

    @OnClick({R.id.rl_purchase_coin, R.id.rl_exchange_coin, R.id.rl_consumption_records,
            R.id.rl_modify_password, R.id.btn_logout, R.id.iv_new_game, R.id.iv_vip_center,
            R.id.iv_purchase_coin, R.id.iv_exchange_mall, R.id.iv_lucky_lottery,
            R.id.sdv_customor_enquiry, R.id.iv_logout, R.id.tv_main_page,
            R.id.iv_high_setting, R.id.iv_manager_setting, R.id.iv_shopping_cart})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_purchase_coin:
                showPurchaseLayout();
                break;
            case R.id.rl_exchange_coin:
                showExchangeCoinLayout();
                break;
            case R.id.rl_consumption_records:
                showConsumptionLayout();
                break;
            case R.id.rl_modify_password:
                showModifyPwdLayout();
                break;
            case R.id.iv_logout:
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
                intent.setClass(this, VIPCenterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_purchase_coin:
                ToastUtil.showToast(this, "已达该页");
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
                intent.setClass(PurchaseCoinActivity.this, HomePageActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_high_setting:
                break;
            case R.id.iv_manager_setting:
                showManagerLayout();
                break;
            case R.id.iv_shopping_cart:

                if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
                    ToastUtil.showToast(this, "请登录!");
                } else if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.user_accessToken, "").toString())) {
                    ToastUtil.showToast(this, "请先在公众号绑卡,再重新登录!");
                } else {
                    ShoppingCartBottomFragment.newInstance().show(getSupportFragmentManager(), ShoppingCartBottomFragment.class.getSimpleName());
                }
                break;
            default:
                break;
        }
    }


    private void showConsumptionLayout() {
        if (LocalDefines.sIsLogin) {
            mTvPurchaseTitle.setText("消费记录查询（一个月内）");
            replaceFragment(ConsumptionRecordFragment.newInstance());
        } else {
            showLoginDialog(new LoginDialogFragment.LoginDialogListener() {
                @Override
                public void onClickListener() {

                }

                @Override
                public void onDismissListener() {
                    //登录后，弹窗消失后执行
                    if (LocalDefines.sIsLogin) {
                        replaceFragment(ConsumptionRecordFragment.newInstance());
                    }
                }
            });
        }
    }

    private void showManagerLayout() {
        if (LocalDefines.sIsLogin) {
            MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);
            if (memberInfo != null && memberInfo.getLevelID() == Constance.machineMangerLevel) {
                TisEditDialog dialog = new TisEditDialog(this).create().setMessage("请输入密码")
                        .setNegativeButton(null)
                        .setPositiveButton(new TisEditDialog.PositiveButtonListener() {
                            @Override
                            public void onClick(View v, String input) {
                                if (input.equals("")) {
                                    new TisDialog(PurchaseCoinActivity.this).create().setMessage("密码不能为空").show();
                                    return;
                                }
                                checkMemberPwd(memberInfo.getId(), input);
                            }
                        }).show();
            } else {
                ToastUtil.showToast(this, "非经理卡禁用此功能");
            }
        } else {
            showLoginDialog(null);
        }
    }

    private void showExchangeCoinLayout() {
        mTvPurchaseTitle.setText("提取游戏币");
        if (LocalDefines.sIsLogin) {
            replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
            FragmentManager manager = getSupportFragmentManager();
            // 这个是从 Container 中找到的Fragment
            Fragment fragment = manager.findFragmentById(R.id.fl_fragment_container);
            if (fragment != null && fragment instanceof PurchaseAndTakeCoinFragment) {
                ((PurchaseAndTakeCoinFragment) fragment).initTakeCoinsList();
            } else {
                Bundle bundle = new Bundle();
                int takeType = 2;
                bundle.putInt("type", takeType);
                PurchaseAndTakeCoinFragment.newInstance().setArguments(bundle);
                replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
            }
        } else {
            showLoginDialog(new LoginDialogFragment.LoginDialogListener() {
                @Override
                public void onClickListener() {

                }

                @Override
                public void onDismissListener() {
                    //登录后，弹窗消失后执行
                    if (LocalDefines.sIsLogin) {
                        replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
                        FragmentManager manager = getSupportFragmentManager();
                        // 这个是从 Container 中找到的Fragment
                        Fragment fragment = manager.findFragmentById(R.id.fl_fragment_container);
                        if (fragment != null && fragment instanceof PurchaseAndTakeCoinFragment) {
                            ((PurchaseAndTakeCoinFragment) fragment).initTakeCoinsList();
                        } else {
                            Bundle bundle = new Bundle();
                            int takeType = 2;
                            bundle.putInt("type", takeType);
                            PurchaseAndTakeCoinFragment.newInstance().setArguments(bundle);
                            replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
                        }
                    }
                }
            });
        }
    }

    private void showPurchaseLayout() {
        FragmentManager manager = getSupportFragmentManager();
        // 这个是从 Container 中找到的Fragment
        Fragment fragment = manager.findFragmentById(R.id.fl_fragment_container);
        if (fragment != null && fragment instanceof PurchaseAndTakeCoinFragment) {
            ((PurchaseAndTakeCoinFragment) fragment).initCoinList();
        } else {
            Bundle bundle = new Bundle();
            int purchaseType = 1;
            bundle.putInt("type", purchaseType);
            PurchaseAndTakeCoinFragment.newInstance().setArguments(bundle);
            replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
        }
    }

    private void showModifyPwdLayout() {
        if (LocalDefines.sIsLogin) {
            mTvPurchaseTitle.setText("修改密码");
            replaceFragment(ModifyPwdFragment.newInstance());
        } else {
            showLoginDialog(new LoginDialogFragment.LoginDialogListener() {
                @Override
                public void onClickListener() {

                }

                @Override
                public void onDismissListener() {
                    //登录后，弹窗消失后执行
                    Log.i(TAG, "Dialog onCancel");
                    if (LocalDefines.sIsLogin) {
                        Log.i(TAG, "Dialog onDismissListener");
                        replaceFragment(ModifyPwdFragment.newInstance());
                    }
                }
            });
        }
    }

    // 购物计时器
    static class ShoppingTimeCount extends CountDownTimer {
        WeakReference<PurchaseCoinActivity> mWeakReference;

        public ShoppingTimeCount(PurchaseCoinActivity purchaseCoinActivity1, long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            mWeakReference = new WeakReference<>(purchaseCoinActivity1);
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            PurchaseCoinActivity purchaseCoinActivity = mWeakReference.get();
            if (purchaseCoinActivity != null) {
                purchaseCoinActivity.mTvPurchaseTitle.setText("选择套餐");
                purchaseCoinActivity.mTvTimerCount.setText("倒计时：00:00");
                //清除会员登录信息
                SharePerferenceUtil.getInstance().setValue(Constance.member_Info, "");
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, "");
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, "");
                //重新初始化
                purchaseCoinActivity.onResume();
            }
        }

        @Override
        public void onTick(long millisUntilFinished) { // 计时过程显示
            PurchaseCoinActivity purchaseCoinActivity = mWeakReference.get();
            if (purchaseCoinActivity != null) {
                if (millisUntilFinished < 10000) {
                    purchaseCoinActivity.mTvTimerCount.setText("倒计时：00:0" + millisUntilFinished / 1000);
                } else {
                    purchaseCoinActivity.mTvTimerCount.setText("倒计时：00:" + millisUntilFinished / 1000);
                }
            }
        }
    }

    /**
     * 开启登录识别扫描器
     */
    private void startLoginRecognitionScan() {
        try {
            //开启扫描器识别
            VbarUtils.getInstance(this)
                    .setScanResultExecListener(new VbarUtils.ScanResultExecListener() {
                        @Override
                        public void scanResultExec(String result) {
                            if (!TextUtils.isEmpty(result)
                                    && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
//                                Log.i(TAG, "startLoginRecognitionScan result = " + result);
                                scanCardLogin(result);
                            }
                        }

                    }).getScanResult();

        } catch (Exception e) {

        }
    }

    /**
     * 会员扫卡登录
     */
    public void scanCardLogin(String scan_result) {
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("CardSN", scan_result);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        KProgressHUD dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetMemberInfoByCardNo, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                Log.i(TAG, "scanCardLogin onSuccessData result = " + result);
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200") &&
                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
                    JsonObject data = jsonResult.getAsJsonObject("Data");

                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, data.toString());

                    String Wid = data.get("WechatID").getAsString();
                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();

                    //显示登录信息
                    showLoginInfo();

                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(PurchaseCoinActivity.this).create()
                            .setMessage("登录成功!").show();


                } else {
                    Toast.makeText(PurchaseCoinActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    startLoginRecognitionScan();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "scanCardLogin onError Throwable = " + ex);
                startLoginRecognitionScan();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (dia != null && dia.isShowing()) {
                    dia.dismiss();
                }
            }
        });
    }

    /**
     * 商城会员登录接口
     */
    private void userLogin(String wxopen_id, String branch_id, String vpn) {
        String MacineId = SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString();
        String Bname = SharePerferenceUtil.getInstance().getValue(Constance.BranchName, "").toString();
        if (TextUtils.isEmpty(MacineId) || TextUtils.isEmpty(Bname)) {
            return;
        }
        HashMap<String, String> map = new HashMap();
//        map.put("open_id","o4hYLwyyF2D0NDjO4aoSjvI47lL8");
//        map.put("branch_id","c5d96d6b-c8ae-48a0-a8a3-ad88edbcc2ab");
//        map.put("child_url","12341");
//        map.put("deviceid","1341234123");
//        map.put("branch_name","1234123");
        map.put("open_id", wxopen_id);
        map.put("branch_id", branch_id);
        map.put("child_url", vpn);
        map.put("deviceid", MacineId);
        map.put("branch_name", Bname);

        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        if (null != memberInfo) {
            map.put("cust_id", memberInfo.getId());
            map.put("mobile", memberInfo.getPhone());
        } else {
            map.put("cust_id", "");
            map.put("mobile", "");
        }

        NetworkRequest.getInstance().userLogin(map, new NetworkCallback<JsonObject>() {

            @Override
            public void onSuccess(JsonObject data) {
                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken, data.get("accessToken").getAsString());
                SharePerferenceUtil.getInstance().setValue(Constance.shop_id, data.get("shop_id").getAsString());

            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    //显示或隐藏登录(登录了机台信息)
    public void showLoginInfo() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            LocalDefines.sIsLogin = false;
            mTimeCount.cancel();
            //未登录状态
            replaceFragment(PurchaseAndTakeCoinFragment.newInstance());
            mTvLoginPlease.setVisibility(View.VISIBLE);
            mTvVipName.setVisibility(View.INVISIBLE);
            mTvlottery.setVisibility(View.INVISIBLE);
            mTvLotteryCount.setVisibility(View.INVISIBLE);
            mTvGameCoin.setVisibility(View.INVISIBLE);
            mTvGameCoinCount.setVisibility(View.GONE);
            mBtnLogout.setVisibility(View.GONE);
//            mIvShoppingCart.setVisibility(View.GONE);


            mBaseActivityHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //开启登录识别扫描器
                    startLoginRecognitionScan();
                    //创建新的微信授权二维码
                    CreateScanCode(SharePerferenceUtil.getInstance().getValue(Constance.MachineID, "").toString());
                }
            }, 500);
        } else {
            LocalDefines.sIsLogin = true;
            dismissLoginDialog();
            mTimeCount.cancel();
            mTimeCount.start();
            //初始化登录信息
            String logininfo = SharePerferenceUtil.getInstance()
                    .getValue(Constance.member_Info, "").toString();
            MemberInfo loginJson = GsonUtil.Companion.jsonToObject(logininfo, MemberInfo.class);

            if (loginJson != null) {
                mTvLoginPlease.setVisibility(View.GONE);
                mTvVipName.setVisibility(View.VISIBLE);
                mTvlottery.setVisibility(View.VISIBLE);
//                mTvLotteryCount.setVisibility(View.VISIBLE);
                mTvGameCoin.setVisibility(View.VISIBLE);
                mTvGameCoinCount.setVisibility(View.VISIBLE);
                mBtnLogout.setVisibility(View.VISIBLE);
                mIvShoppingCart.setVisibility(View.VISIBLE);
                try {
                    mTvLotteryCount.setText(loginJson.getTickets().substring(0, loginJson.getTickets().indexOf(".")));
                    mTvGameCoinCount.setText(loginJson.getCoins().substring(0, loginJson.getCoins().indexOf(".")));

                } catch (Exception e) {
                    mTvLotteryCount.setText(loginJson.getCoins());
                    mTvGameCoinCount.setText(loginJson.getTickets());
                }
                mTvVipName.setText(loginJson.getCustName());
            }

        }
    }

    /**
     * 生成授权微信登陆二维码
     */
    public void CreateScanCode(String MachineID) {
        if (TextUtils.isEmpty(MachineID)) {
            return;
        }
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("MachineID", MachineID);
        hashmap.put("MenuName", "扫码登录");
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.CreateScanCode, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200")) {
                    Log.i(TAG, "CreateScanCode result = " + result);
                    wx_qrcode = jsonResult.get("Data").getAsString();
                    if (!TextUtils.isEmpty(wx_qrcode)) {
                        try {
                            mIvWxQRCode.setImageBitmap(QRBitmapUtils.createQRCode(wx_qrcode, 450));
                        } catch (Exception e) {
                            Log.i(TAG, "CreateScanCode Exception = " + e.toString());
                        }

                    }
                    Log.i(TAG, "wx_qrcode result = " + wx_qrcode);
                    String TmpGuid = jsonResult.get("Data2").getAsString();
                    wx_handle.removeCallbacksAndMessages(null);
                    //开始循环接口，查看是否登录
                    wx_handle.post(new Runnable() {
                        @Override
                        public void run() {
                            if (TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
                                authorizedLogin(TmpGuid);
                            }
                        }

                    });

                    //刷新会员界面
//                    manage.beginTransaction().replace(R.id.main_fragment_content,new MemberCenterFragment()).commitAllowingStateLoss();

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i(TAG, "wx_qrcode onError ");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(TAG, "wx_qrcode onCancelled ");
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 微信授权扫码登录
     */
    private void authorizedLogin(String TmpGuid) {
        if (!TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
            return;
        }
        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("TempGuid", TmpGuid);
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetCustomerScanData, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200") &&
                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
                    wx_qrcode = "";
                    JsonObject data = jsonResult.getAsJsonObject("Data");
                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, data.toString());

                    String Wid = data.get("WechatID").getAsString();
                    String Bid = SharePerferenceUtil.getInstance().getValue(Constance.BranchID, "").toString();
                    String Vpn = SharePerferenceUtil.getInstance().getValue(Constance.Vpn, "").toString();

                    //显示登录信息
                    showLoginInfo();

                    //登录商城
//                    userLogin(Wid,Bid,Vpn);
                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(PurchaseCoinActivity.this).create()
                            .setMessage("登录成功!").show();


                } else {
                    wx_handle.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            authorizedLogin(TmpGuid);
                        }

                    }, 2500);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                wx_handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        authorizedLogin(TmpGuid);
                    }

                }, 2500);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void checkMemberPwd(String custID, String pwd) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("CustID", custID);
        hashMap.put("PassWord", pwd);
        hashMap.put("sign", SignParamUtil.getSignStr(hashMap));

        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.CheckCustomerPassword, hashMap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonObject = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonObject.has("return_Code") && jsonObject.get("return_Code").equals("200")) {
//                    startActivity(new Intent(PurchaseCoinActivity.this, ErrorHandleActivity.class));
                    replaceFragment(ManagerSystemFragment.newInstance());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        Log.i(TAG, "replaceFragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        transaction.replace(R.id.fl_fragment_container, fragment);
        transaction.commit();
    }
}
