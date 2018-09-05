package com.qy.zgz.mall.page.index;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.qy.zgz.mall.BaseActivity;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.lcb_game.NumDanceActivity;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.index_function.CustomerServiceDialog;
import com.qy.zgz.mall.slot_machines.game.SlotMachinesActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.MyTextView;
import com.qy.zgz.mall.widget.TisDialog;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class HomePageActivity extends BaseActivity {
    @BindView(R.id.sdv_main_title_img)
    ImageView mSdvTitleImg;

    @BindView(R.id.sdv_customor_enquiry)
    ImageView mSdvCustomorEnquiry;

    @BindView(R.id.sdv_main_page)
    ImageView mSdvMainPage;
    @BindView(R.id.sdv_vip_center)
    ImageView mSdvVipCenter;
    @BindView(R.id.sdv_exchange_coin)
    ImageView mSdvExchangeCoin;
    @BindView(R.id.sdv_mall)
    ImageView mSdvMall;
    @BindView(R.id.sdv_lucky_lottery)
    ImageView mSdvLuckyLottery;
    @BindView(R.id.iv_shopping_cart)
    ImageView mIvShoppingCart;
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    @BindView(R.id.rl_mall)
    RelativeLayout mRlMall;
    @BindView(R.id.iv_high_setting)
    ImageView mIvSetting;
    @BindView(R.id.layout_bottom_tab)
    AutoLinearLayout mLlBottomTab;

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
    @BindView(R.id.tv_main_page)
    TextView mTvMainPage;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;

    private String mCinemaType;
    private String mCinemaid;
    public final static String TAG = "HomePageActivity";

    public static String wx_qrcode = "";

    public DbManager db;

    //检查微信登录handle
    private Handler wx_handle = new Handler();

    @Override
    public void createView() {
        setContentView(R.layout.activity_mall_main);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState, @Nullable Intent intent) {
        if (getIntent() != null && getIntent().getStringExtra("cinemaType") != null && getIntent().getStringExtra("cinemaid") != null) {
            Log.i(TAG, "getIntent != null mCinemaType = " + mCinemaType + " mCinemaid = " + mCinemaid);
            mCinemaType = (String) SharePerferenceUtil.getInstance().getValue("typeId", "");
            mCinemaid = (String) SharePerferenceUtil.getInstance().getValue("cinemaid", "");
        }
        mTvShopName.setText(SharePerferenceUtil.getInstance().getValue("type_shop_name","").toString());
//        initDataManager();
        initImageRecsourse();
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
        countDownTimer.cancel();
        mBaseActivityHandler.removeCallbacksAndMessages(null);
        wx_handle.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        initData();
    }

    //用户操作监听
    @Override
    public void onUserInteraction() {
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

    private void viewLongClickListener(){
        mIvSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new UnityDialog(HomePageActivity.this).setHint("是否更换店铺")
                        .setCancel("取消", null)
                        .setConfirm("确定", new UnityDialog.OnConfirmDialogListener() {
                            @Override
                            public void confirm(UnityDialog unityDialog, String content) {
                                SharePerferenceUtil.getInstance().setValue("typeId", "");
                                SharePerferenceUtil.getInstance().setValue("cinemaid", "");
                                SharePerferenceUtil.getInstance().setValue("type_shop_name", "");
                                //清除机器场地BranchID
                                SharePerferenceUtil.getInstance().setValue(Constance.BranchID,"");
                                //清除机器ID
                                SharePerferenceUtil.getInstance().setValue(Constance.MachineID,"");
                                //清除机器VPN
                                SharePerferenceUtil.getInstance().setValue(Constance.Vpn,"");
                                //清除会员登录信息
                                SharePerferenceUtil.getInstance().setValue(Constance.member_Info,"");
                                //清除商城会员登录accessToken
                                SharePerferenceUtil.getInstance().setValue(Constance.user_accessToken,"");
                                //清除商城会员登录shop_id
                                SharePerferenceUtil.getInstance().setValue(Constance.shop_id,"");
                                MyApplication.getInstance().restartApp();
                            }
                        });
                return false;
            }
        });
    }

    private void initImageRecsourse() {
        mLlBottomTab.setVisibility(View.GONE);
    }

    private void initDataManager() {
        //数据库配置
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                // 数据库的名字
                .setDbName("kmd")
                // 保存到指定路径
                .setDbDir(new
                        File(Environment.getExternalStorageDirectory().getPath() + "/hyppmm/"))
//                .setDbOpenListener(new DbManager.DbOpenListener() {
//                 @Override
//                    public void onDbOpened(DbManager db) {
//                        // 开启WAL, 对写入加速提升巨大
//                        db.getDatabase().enableWriteAheadLogging();
//                    }
//                })
                // 数据库的版本号
                .setDbVersion(1);
        db = x.getDb(daoConfig);
    }

    private void initData() {
        //清除会员登录信息
        SharePerferenceUtil.getInstance()
                .setValue(Constance.member_Info, "");
        //清除商城会员登录accessToken
        SharePerferenceUtil.getInstance()
                .setValue(Constance.user_accessToken, "");
        //清除商城会员登录shop_id
        SharePerferenceUtil.getInstance()
                .setValue(Constance.shop_id, "");
    }

    @OnClick({R.id.rl_mall, R.id.rl_exchange_coin, R.id.rl_vip_center, R.id.btn_logout, R.id.rl_new_game, R.id.rl_lucky_lottery,
    R.id.sdv_customor_enquiry, R.id.tv_main_page})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.rl_mall:
                intent.setClass(HomePageActivity.this, MallActivity.class);
//                intent.putExtra("cinemaType", mCinemaType);
//                intent.putExtra("cinemaid", mCinemaid);
                HomePageActivity.this.startActivity(intent);
                break;
            case R.id.rl_exchange_coin:
                intent.setClass(HomePageActivity.this, PurchaseCoinActivity.class);
//                intent.putExtra("cinemaType", mCinemaType);
//                intent.putExtra("cinemaid", mCinemaid);
                HomePageActivity.this.startActivity(intent);
                break;
            case R.id.rl_vip_center:
                intent.setClass(HomePageActivity.this, VIPCenterActivity.class);
//                intent.putExtra("cinemaType", mCinemaType);
//                intent.putExtra("cinemaid", mCinemaid);
                HomePageActivity.this.startActivity(intent);
                break;
            case R.id.btn_logout:
//                logout(this);
                VipLogout();
                showLoginInfo();
                break;
            case R.id.rl_new_game:
//                ToastUtil.showToast(this, "已是首页");
                intent.setClass(HomePageActivity.this, NumDanceActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_lucky_lottery:
                intent.setClass(this, SlotMachinesActivity.class);
                startActivity(intent);
                break;
            case R.id.sdv_customor_enquiry:
                new CustomerServiceDialog(this).create().show();
                break;
            case R.id.tv_main_page:
//                intent.setClass(HomePageActivity.this, HomePageActivity.class);
//                startActivity(intent);
                ToastUtil.showToast(this, "已是首页");
                break;
            default:
                break;
        }
    }

//    /**
//     * 开启登录识别扫描器
//     */
    private void startLoginRecognitionScan() {
        try {
            //开启扫描器识别
            VbarUtils.getInstance(this)
                    .setScanResultExecListener(new VbarUtils.ScanResultExecListener() {
                        @Override
                        public void scanResultExec(String result) {
                            if (!TextUtils.isEmpty(result)
                                    && TextUtils.isEmpty(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString())) {
                                Log.i(TAG, "startLoginRecognitionScan result = " + result);
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
                    new TisDialog(HomePageActivity.this).create()
                            .setMessage("登录成功!").show();

                } else {
                    Toast.makeText(HomePageActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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

        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString(), MemberInfo.class);

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
    private void showLoginInfo() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {

            countDownTimer.cancel();
            //未登录状态
            mTvLoginPlease.setVisibility(View.GONE);
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
                    CreateScanCode(SharePerferenceUtil.getInstance()
                            .getValue(Constance.MachineID, "").toString());
                }
            }, 500);
        } else {
            countDownTimer.cancel();
            countDownTimer.start();
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
                mBtnLogout.setBackgroundResource(R.drawable.shape_logout);
                mBtnLogout.setClickable(true);
//                mIvShoppingCart.setVisibility(View.VISIBLE);
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

    //执行倒计时30秒
    private CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
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
            onResume();
        }

    };

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

                    wx_qrcode = jsonResult.get("Data").getAsString();
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

            }

            @Override
            public void onCancelled(CancelledException cex) {

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
                    new TisDialog(HomePageActivity.this).create()
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
}
