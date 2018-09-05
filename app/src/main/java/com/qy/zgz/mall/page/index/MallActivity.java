package com.qy.zgz.mall.page.index;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kevin.wraprecyclerview.WrapRecyclerView;
import com.qy.zgz.mall.BaseActivity;
import com.qy.zgz.mall.Model.Cinemadata;
import com.qy.zgz.mall.Model.CinemadataCategory;
import com.qy.zgz.mall.Model.Cranemaapi;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.Model.Version;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.SmallImageAdapter;
import com.qy.zgz.mall.adapter.SmallImagePager;
import com.qy.zgz.mall.lcb_game.NumDanceActivity;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.network.NetworkCallback;
import com.qy.zgz.mall.network.NetworkRequest;
import com.qy.zgz.mall.network.XutilsCallback;
import com.qy.zgz.mall.page.fragment.AddressAddFragment;
import com.qy.zgz.mall.page.fragment.BalanceFragment;
import com.qy.zgz.mall.page.fragment.MemberCenterFragment;
import com.qy.zgz.mall.page.fragment.MenberCenterLoginingFragment;
import com.qy.zgz.mall.page.fragment.ShopCarFragment;
import com.qy.zgz.mall.page.index_function.CustomerServiceDialog;
import com.qy.zgz.mall.page.index_function.IndexFuncitonActivity;
import com.qy.zgz.mall.page.max.MaxImageActivity;
import com.qy.zgz.mall.page.money_purchase.BuyCoinsActivity;
import com.qy.zgz.mall.slot_machines.game.SlotMachinesActivity;
import com.qy.zgz.mall.utils.AnimatorUtil;
import com.qy.zgz.mall.utils.AntoUtil;
import com.qy.zgz.mall.utils.DownFileUtil;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.FrescoUtils;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.HttpUtils;
import com.qy.zgz.mall.utils.InputUtils;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.SignParamUtil;
import com.qy.zgz.mall.utils.TimeUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.utils.UnityDialog;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.qy.zgz.mall.widget.ClearEditText;
import com.qy.zgz.mall.widget.MyGridLayoutManager;
import com.qy.zgz.mall.widget.MyTextView;
import com.qy.zgz.mall.widget.MyTextWatch;
import com.qy.zgz.mall.widget.NoScrollViewPager;
import com.qy.zgz.mall.widget.SpaceItemDecoration;
import com.qy.zgz.mall.widget.TisDialog;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.youth.banner.Banner;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.angmarch.views.NiceSpinner;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 广告首页
 */
public class MallActivity extends BaseActivity {

    private String cinemaType = "1";
    private String cinemaId = "";


    @BindView(R.id.ll_dot)
    public AutoLinearLayout llDot;

    @BindView(R.id.iv_red_packet)
    public ImageView ivRedPacket;

    @BindView(R.id.vpager)
    public NoScrollViewPager viewPager;

    @BindView(R.id.v_updateversion)
    public View vUpdateVersion;

    private SmallImagePager helperPager;

    @BindView(R.id.btn_gotoset)
    public Button btnSet;

    @BindView(R.id.ban_main_banner)
    public Banner ban_main_banner;

    @BindView(R.id.gv_main_info)
    public WrapRecyclerView gv_main_info;

//    @BindView(R.id.btn_main_hot_search)
//    public TextView btn_main_hot_search;

//    @BindView(R.id.btn_main_price_search)
//    public TextView btn_main_price_search;

    @BindView(R.id.cet_main_search)
    public ClearEditText cet_main_search;

    @BindView(R.id.tbtn_main_toggle_search)
    public ToggleButton tbtn_main_toggle_search;

    @BindView(R.id.et_main_low_price)
    public EditText et_main_low_price;

    @BindView(R.id.et_main_high_price)
    public EditText et_main_high_price;

    @BindView(R.id.ns_main_nice_spinner)
    public NiceSpinner ns_main_nice_spinner;

    @BindView(R.id.all_main_price_head)
    public AutoLinearLayout all_main_price_head;

    @BindView(R.id.btn_main_go_index_page)
    public Button btn_main_go_index_page;

    @BindView(R.id.arl_main_unlogin)
    public AutoRelativeLayout arl_main_unlogin;

    @BindView(R.id.all_main_logining)
    public AutoLinearLayout all_main_logining;

    @BindView(R.id.btn_main_add_car)
    public Button btn_main_add_car;

    @BindView(R.id.tv_main_username)
    public TextView tv_main_username;

    @BindView(R.id.tv_main_user_tickets)
    public TextView tv_main_user_tickets;

    @BindView(R.id.tv_main_user_coins)
    public TextView tv_main_user_coins;

    @BindView(R.id.arl_main_bottom_content)
    public AutoRelativeLayout arl_main_bottom_content;

    @BindView(R.id.main_fragment_content)
    public AutoFrameLayout main_fragment_content;

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
    @BindView(R.id.btn_logout)
    Button mBtnLogout;
    @BindView(R.id.iv_high_setting)
    ImageView mIvSetting;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.iv_shopping_cart)
    ImageView mIvShoppingCart;

    //下拉列表数据
    ArrayList<String> nsData = new ArrayList<>();

    private SmallImageAdapter search_adapter;

    private List<TextView> dotView = new ArrayList<>();
    //大图片index
    private int bigIndex = 0;
    //中图片index
    private int mediumIndex = 0;

    private int videoIndex = 0;

    private PredefineCustomizationDialog pcz_diaolog;

    Handler handler;
    //数据
    Cranemaapi cranemaapi;
    //小图页数
    private int pageNum = 0;

    //记录红包是否显示:1--显示，0--不显示
    private String red_type = "0";

    //判断是否初始化
    public static boolean isInit = true;

    //判断是否跳到购物车结算页面(只提供立即结算市使用)
    public static boolean isGoMall = false;

    //判断是否跳到未登录页面
    public static boolean isUnLogin = false;

    //扫码登录授权二维码链接
    public static String wx_qrcode = "";


    //fragment管理
    private FragmentManager manage;

    private Context mContext = this;

    private Toast toast;

    //检查微信登录handle
    private Handler wx_handle = new Handler();

    //红包促销商品信息
    private Cinemadata red_packet_cinema = null;

    //红包点击是否跳转商品信息
    private String is_skip = "0";

//    private boolean isTest=false;


    @Override
    public void createView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void afterCreate(@Nullable Bundle savedInstanceState, @Nullable Intent intent) {
        mContext = this;
        handler = new Handler();
//        cinemaType = getIntent().getStringExtra("cinemaType");
//        cinemaId = getIntent().getStringExtra("cinemaid");
        cinemaType = (String) SharePerferenceUtil.getInstance().getValue("typeId", "");
        cinemaId = (String) SharePerferenceUtil.getInstance().getValue("cinemaid", "");
        Constance.lastTouchTime = System.currentTimeMillis();
        manage = getSupportFragmentManager();
        toast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);//初始化一个toast，解决多次弹出toast冲突问题
        initView();
        initData();
        //检查更新版本
//        updateVersion();

        btnSet.setOnClickListener(new gotosetCLick());
        handler.postDelayed(noTouchRunnable, 60 * 1000);
        viewLongClickListener();
    }

    private void initView() {
        new AnimatorUtil().screenTranslation(ivRedPacket);
        mTvShopName.setText(SharePerferenceUtil.getInstance().getValue("type_shop_name","").toString());
    }

    /**
     * 初始化数据
     */
    private void initData() {

        AutoUtils.auto(tv_main_user_coins);
        AutoUtils.auto(tv_main_user_tickets);
        AutoUtils.auto(tv_main_username);
        cet_main_search.addTextChangedListener(MyTextWatch.getInstance(this));
        et_main_low_price.addTextChangedListener(MyTextWatch.getInstance(this));
        et_main_high_price.addTextChangedListener(MyTextWatch.getInstance(this));
        Glide.with(this)
                .load(R.drawable.hand)
                .into(ivRedPacket);
        //请求红包接口
        getRedPacket();
        tbtn_main_toggle_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cet_main_search.setText("");
                et_main_low_price.setText("");
                et_main_high_price.setText("");
                InputUtils.Companion.closeInput(MallActivity.this);
                if (isChecked) {
                    //按票数搜索
                    cet_main_search.setInputType(InputType.TYPE_CLASS_NUMBER);
                    cet_main_search.setHint("请输入商品票数");
                    all_main_price_head.setVisibility(View.VISIBLE);
                    cet_main_search.setVisibility(View.GONE);
                } else {
                    //按商品搜索
                    cet_main_search.setInputType(InputType.TYPE_CLASS_TEXT);
                    cet_main_search.setHint("请输入商品关键字");
                    all_main_price_head.setVisibility(View.GONE);
                    cet_main_search.setVisibility(View.VISIBLE);
                }
            }
        });
        ns_main_nice_spinner.setGravity(Gravity.CENTER);
//        ns_main_nice_spinner.setEnabled(false);
        ns_main_nice_spinner.setTextColor(getResources().getColor(R.color.color_black));
        ns_main_nice_spinner.setBackgroundColor(Color.parseColor("#E1E1E0"));
        //控件按比例大小自动化
        AutoUtils.auto(ns_main_nice_spinner);
        AutoUtils.auto(et_main_low_price);
        AutoUtils.auto(et_main_high_price);
        AutoUtils.auto(cet_main_search);
        //配置商品显示的设置(搜索商品)
        gv_main_info.setLayoutManager(new MyGridLayoutManager(MallActivity.this, 4));
        gv_main_info.addItemDecoration(new SpaceItemDecoration(AutoUtils.getPercentWidthSize(20)));

        ArrayList<Integer> banList = new ArrayList<>();
        banList.add(R.drawable.banner);
        //banner设置
        ban_main_banner.setDelayTime(10000);//设置轮播时间
        ban_main_banner.setImageLoader(new GlideImageLoader());
        ban_main_banner.setImages(banList).start();
        pageNum = 40;

        String json = (String) SharePerferenceUtil.getInstance().getValue("cranemaapi", "");
        long time = 0L;
        long lastGetBannerTime = (long) SharePerferenceUtil.getInstance().getValue(LocalDefines.LAST_GET_BANNER_TIME, time);
        Gson gson = new Gson();
        if (json != null && !json.equals("")) {
            cranemaapi = gson.fromJson(json, Cranemaapi.class);
            if (null != cranemaapi.getImages() && !cranemaapi.getImages().isEmpty() /*|| System.currentTimeMillis() - lastGetBannerTime < 43200000*/) {
                initDot();
//                showData();
//                getImageList();
            } else {
//                getCranema();
            }
        } else {
//            getCranema();
        }
        getCranema();
    }

    private void viewLongClickListener() {
        mIvSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new UnityDialog(MallActivity.this).setHint("是否更换店铺")
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

    private void getCranema() {
        NetworkRequest.getInstance().getCranemaapi(cinemaType, cinemaId, new NetworkCallback<Cranemaapi>() {
            @Override
            public void onSuccess(Cranemaapi data) {
                Log.i("getCranemaFromServer", "getCranemaFromServer data = " + data);
                MallActivity.this.cranemaapi = data;
                Gson gson = new Gson();
                String json = gson.toJson(data);
                SharePerferenceUtil.getInstance().setValue("cranemaapi", json);
                SharePerferenceUtil.getInstance().setValue(LocalDefines.LAST_GET_BANNER_TIME, System.currentTimeMillis());
                initDot();
//                isTest=true;
                showData();
                if (TimeUtil.getInstance().isExceedTime()) {
                    FileManager.getInstance().deleteSvaeFile();
                    FileManager.getInstance().deleteAPKSvaeFile();
                }
                //下载图片
                getImageList();
                handler.removeCallbacks(getCranRunnable);
            }

            @Override
            public void onFailure(int code, String msg) {
                Log.i("getCranemaFromServer", "getCranemaFromServer onFailure");
                ToastUtil.showToast(MallActivity.this, msg);
                handler.postDelayed(getCranRunnable, 5000);
            }

            @Override
            public void onCompleted() {
                Log.i("getCranemaFromServer", "getCranemaFromServer onCompleted");
                showData();
                super.onCompleted();
            }
        });
    }

    private Runnable getCranRunnable = new Runnable() {
        @Override
        public void run() {
            getCranema();
        }
    };

    public void gotosettting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    //    public void doublelongclick(){
//
//
//    }
    private int count = 0;

    private void doublelongclick() {
        Timer tExit = null;
        if (count < 10) {
            count = count + 1;
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    count = 0;
                }
            }, 10000);

        } else {
            count = 0;
            gotosettting();
        }
    }


    public class gotosetCLick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            doublelongclick();
        }
    }

    public class tvOnClick implements View.OnClickListener {
        int postion;

        public tvOnClick(int postion) {
            this.postion = postion;
        }

        @Override
        public void onClick(View v) {
            //显示商品列表,隐藏购物车及会员信息等FRAGMENT
            arl_main_bottom_content.setVisibility(View.VISIBLE);
            main_fragment_content.setVisibility(View.GONE);
//            Log.e("i",postion+"");
            //转转盘
            if (postion == 9) {
                if (cinemaType != "20") {
                    startActivity(new Intent(MallActivity.this, SlotMachinesActivity.class));

                }
                return;
            }
            gv_main_info.setVisibility(View.GONE);
            viewPager.setVisibility(View.VISIBLE);
            dotView.get(postion).setTextColor(getResources().getColor(R.color.colorAccent));
            viewPager.setCurrentItem(postion);
        }
    }

    //添加小图dot
    private void initDot() {
        int dotSum = 10;
        AutoUtils.auto(llDot);
        AutoUtils.autoed(llDot);
        //菜单名字
        String[] menu_name = new String[]{"热门", "玩具", "文体",
                "生活", "数码", "电器", "礼品", "京东", "卡券", "转转盘"};
        int[] menu_img = new int[]{R.drawable.nav01, R.drawable.nav02, R.drawable.nav03
                , R.drawable.nav04, R.drawable.nav05, R.drawable.nav06, R.drawable.nav07
                , R.drawable.nav08, R.drawable.nav10, R.drawable.nav11};
        llDot.removeAllViews();
        dotView.clear();
        for (int i = 0; i < dotSum; i++) {

            AutoLinearLayout autoLinearLayout = new AutoLinearLayout(this);
            autoLinearLayout.setOrientation(AutoLinearLayout.VERTICAL);
            autoLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(AutoLinearLayout.LayoutParams.WRAP_CONTENT, AutoLinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            autoLinearLayout.setGravity(Gravity.CENTER);

            TextView view = new TextView(this);
            AutoUtils.auto(view);
            AutoUtils.autoed(view);
            view.setTextSize(AutoUtils.getPercentWidthSize(36));
            Drawable drawable = getResources().getDrawable(menu_img[i]);
            drawable.setBounds(0, 0, AutoUtils.getPercentWidthSize(drawable.getIntrinsicWidth()), AutoUtils.getPercentHeightSize(drawable.getIntrinsicHeight()));
            view.setCompoundDrawables(null, drawable, null, null);
            view.setGravity(Gravity.CENTER);
            view.setOnClickListener(new tvOnClick(i));
//            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(AutoUtils.getPercentWidthSize(155),AutoUtils.getPercentWidthSize(155));
            AutoLinearLayout.LayoutParams layoutParams = new AutoLinearLayout.LayoutParams(AutoLinearLayout.LayoutParams.WRAP_CONTENT, AutoLinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
//                view.setText(cranemaapi.getCinemasmalldata().get(i).category_name);
                view.setTextColor(getResources().getColor(R.color.colorAccent));
                // view.setBackground(getResources().getDrawable(R.drawable.share_red));
            } else {
//                view.setText(cranemaapi.getCinemasmalldata().get(i).category_name);
                view.setTextColor(getResources().getColor(R.color.color_black));
                //view.setBackground(getResources().getDrawable(R.drawable.share_white));
            }
            view.setLayoutParams(layoutParams);
            view.setText(menu_name[i]);
            view.setSingleLine();
            dotView.add(view);
            autoLinearLayout.addView(view);
            llDot.addView(autoLinearLayout);
        }
    }

    /**
     * 显示数据
     */
    private void showData() {
        //初始化搜索下拉列表
        nsData.clear();
        nsData.add("全部产品");
        try {
            ArrayList<CinemadataCategory> smalldataList = cranemaapi.getCinemasmalldata();
            for (CinemadataCategory ccy : smalldataList) {
                nsData.add(ccy.category_name);
            }
        } catch (Exception e) {

        }
        ns_main_nice_spinner.attachDataSource(nsData);

        mediumIndex = 0;
        bigIndex = 0;
        videoIndex = 0;

        //轮播图
        if (null != cranemaapi.getImages() && !cranemaapi.getImages().isEmpty()) {
            try {
                ban_main_banner.update(cranemaapi.getImages());
            } catch (Exception e) {

            }
        }
        getSmallList();
    }

    @OnClick({R.id.arl_main_activity_root, R.id.iv_main_gosearch, R.id.iv_red_packet, R.id.v_updateversion,
            R.id.btn_logout, R.id.iv_new_game, R.id.iv_vip_center,
            R.id.iv_purchase_coin, R.id.iv_exchange_mall, R.id.iv_lucky_lottery,
            R.id.sdv_customor_enquiry, R.id.tv_main_page})
    public void onClick(View view) {
        Intent intent = new Intent();
        if (cranemaapi == null) {
            return;
        }
        Cinemadata cinema = null;
        switch (view.getId()) {
            case R.id.iv_red_packet:
//                Intent intent = new Intent(this, MaxImageActivity.class);
//                intent.putExtra("cinema", red_packet_cinema);
//                startActivity(intent);
                break;
            case R.id.v_updateversion:
                updateVersion();
                break;
            //调用搜索商品接口
            case R.id.iv_main_gosearch:
                //关闭输入法
                InputUtils.Companion.closeInput(this);
                if (tbtn_main_toggle_search.isChecked()) {

                } else if (TextUtils.isEmpty(cet_main_search.getText().toString().trim())) {
                    return;
                }
                //搜索商品
                HashMap<String, String> map = new HashMap<>();
                map.put("shop_id", cinemaType);
                if (tbtn_main_toggle_search.isChecked()) {
                    if (!TextUtils.isEmpty(et_main_high_price.getText().toString().trim())) {
                        map.put("max_tickets", et_main_high_price.getText().toString().trim());
                    }

                    if (!TextUtils.isEmpty(et_main_low_price.getText().toString().trim())) {
                        map.put("min_tickets", et_main_low_price.getText().toString().trim());
                    }
                    //获取类别ID
                    try {
                        String category_name = nsData.get(ns_main_nice_spinner.getSelectedIndex());
                        if (category_name.equals("全部产品") || TextUtils.isEmpty(category_name)) {
                            map.put("category_id", "");
                        } else {
                            ArrayList<CinemadataCategory> smalldataList = cranemaapi.getCinemasmalldata();
                            for (CinemadataCategory ccy : smalldataList) {
                                if (category_name.equals(ccy.category_name)) {
                                    map.put("category_id", ccy.category_id + "");
                                    break;
                                }
                            }

                        }
                    } catch (Exception e) {
                        return;
                    }
                } else {
                    map.put("search_keywords", cet_main_search.getText().toString().trim());
                }
                KProgressHUD dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("请稍后...").show();
                NetworkRequest.getInstance().getSearch(map, new NetworkCallback<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject data) {
                        try {
                            arl_main_bottom_content.setVisibility(View.VISIBLE);
                            main_fragment_content.setVisibility(View.GONE);
                            if (data.isJsonNull() || !data.has("cinemaData") || data.getAsJsonArray("cinemaData").size() == 0) {
                                ToastUtil.showToast(MallActivity.this, "浏览更多商品，请扫描二维码关注商城");
                                return;
                            }

                            Gson gson = new Gson();
                            search_adapter = new SmallImageAdapter(gson.fromJson(data.getAsJsonArray("cinemaData").toString(), new TypeToken<ArrayList<Cinemadata>>() {

                            }.getType()));
                            gv_main_info.setAdapter(search_adapter);
                            gv_main_info.addFooterView(LayoutInflater.from(mContext).inflate(R.layout.item_recycle_bottom, null));
                            gv_main_info.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.GONE);
                            for (TextView view : dotView) {
                                view.setTextColor(getResources().getColor(R.color.color_black));
                            }


                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            return;
                        }


                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        ToastUtil.showToast(MallActivity.this, msg);
                    }

                    @Override
                    public void onCompleted() {
                        if (dia != null && dia.isShowing()) {
                            dia.dismiss();
                        }

                        super.onCompleted();
                    }
                });
                break;
            //隐藏键盘
            case R.id.arl_main_activity_root:
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                break;
            //返回首页
            case R.id.btn_main_go_index_page:
                startActivity(new Intent(this, HomePageActivity.class));
                // 定义出入场动画
                overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
                break;
            //退出登录
            case R.id.btn_main_exit_login:
                if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                        .getValue(Constance.member_Info, "").toString())) {
                    return;
                }

                //清除会员登录信息
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.member_Info, "");
                //清除商城会员登录accessToken
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.user_accessToken, "");
                //清除商城会员登录shop_id
                SharePerferenceUtil.getInstance()
                        .setValue(Constance.shop_id, "");

                onResume();
                ToastUtil.showToast(this, "退出成功！");
                break;
            //会员中心
            case R.id.btn_main_menber_center:
                arl_main_bottom_content.setVisibility(View.GONE);
                main_fragment_content.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                        .getValue(Constance.member_Info, "").toString())) {
                    manage.beginTransaction().replace(R.id.main_fragment_content, new MemberCenterFragment()).commit();
                } else {
                    manage.beginTransaction().replace(R.id.main_fragment_content, new MenberCenterLoginingFragment()).commit();
                }
                break;
            //购物车
            case R.id.btn_main_add_car:
                if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                        .getValue(Constance.member_Info, "").toString())) {
                    manage.beginTransaction().replace(R.id.main_fragment_content, new MemberCenterFragment()).commit();
                    arl_main_bottom_content.setVisibility(View.GONE);
                    main_fragment_content.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                        .getValue(Constance.user_accessToken, "").toString())) {

                    CToast("请先在公众号绑卡,再重新登录!");
                } else {
                    manage.beginTransaction().replace(R.id.main_fragment_content, new ShopCarFragment()).commit();
                    arl_main_bottom_content.setVisibility(View.GONE);
                    main_fragment_content.setVisibility(View.VISIBLE);
                }
                break;
            //预约定制
            case R.id.iv_main_predefine:
                pcz_diaolog = new PredefineCustomizationDialog(MallActivity.this).create()
                        .setWebUrl(cranemaapi.getGiftCust(), cinemaType).show();
                break;
            //去登录
            case R.id.btn_main_gologin:
                arl_main_bottom_content.setVisibility(View.GONE);
                main_fragment_content.setVisibility(View.VISIBLE);
                manage.beginTransaction().replace(R.id.main_fragment_content, new MemberCenterFragment()).commit();
                break;
            //购币取币
            case R.id.btn_main_go_buy_coins:
                startActivity(new Intent(this, BuyCoinsActivity.class));
                break;
            case R.id.btn_logout:
//                logout(this);
                VipLogout();
                isLogining();
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
                intent.setClass(this, PurchaseCoinActivity.class);
                startActivity(intent);
                finish();
//                ToastUtil.showToast(this,"已达该页");
                break;
            case R.id.iv_exchange_mall:
                ToastUtil.showToast(this, "已达该页");
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
                intent.setClass(MallActivity.this, HomePageActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    /**
     * 跳转到大图
     *
     * @param cinemadata
     */
//    private void intentMax(Cinemadata cinemadata) {
//        Intent intent = new Intent(this, MaxImageActivity.class);
//        intent.putExtra("cinema", cinemadata);
//        this.startActivity(intent);
//    }

    /**
     * 获取点击的中图下标
     *
     * @param index
     * @return
     */
//    private int getMediumIndex(int index) {
//        if (index < 0) {
//            index = cranemaapi.getCinemamediumdata().size() + index;
//        }
//        return index;
//    }


    /**
     * 显示中图信息
     *
     * @param image
     */
//    private void showMediuImage(SimpleDraweeView image) {
//        String fileName = FileManager.getInstance().getFileName(cranemaapi.getCinemamediumdata().get(mediumIndex).getThumb());
//        Uri uri = null;
//        if (FileManager.getInstance().isFileExists(fileName)) {
//            uri = Uri.parse("file://" + FileManager.getInstance().getDestFileDir() + fileName);
//        } else {
//            uri = Uri.parse(cranemaapi.getCinemamediumdata().get(mediumIndex).getThumb());
//        }
//        FrescoUtils.showThumb(image, uri, AutoUtils.getPercentWidthSize(826), AutoUtils.getPercentHeightSize(685));
//
//        mediumIndex++;
//        if (mediumIndex >= cranemaapi.getCinemamediumdata().size()) {
//            mediumIndex = 0;
//        }
//    }

    /**
     * 小图定位器
     */
    private int pagerIndex = 1;


    /**
     * 获取要下载的文件列表
     */
    private List<String> imageList;

    private void getImageList() {
        imageList = new ArrayList<>();

        //下载大图片（热门图片）
        ArrayList<Cinemadata> bigdataList = cranemaapi.getCinemabigdata();
        for (Cinemadata cinemadata : bigdataList) {
            imageList.add(cinemadata.getImage_default_id());
            List<String> cimg = cinemadata.getThumb();
            for (String img : cimg) {
                imageList.add(img);
            }
            imageList.add(cinemadata.getQcode());
        }
        //下载小图片
        ArrayList<CinemadataCategory> smalldataList = cranemaapi.getCinemasmalldata();
        int smallSize = smalldataList.size();
        for (int i = 0; i < smallSize; i++) {
            if (smalldataList.get(i) != null && smalldataList.get(i).category_data != null) {
                ArrayList<Cinemadata> categoryData = smalldataList.get(i).category_data;
                for (Cinemadata cinemadata : categoryData) {
                    imageList.add(cinemadata.getImage_default_id());
                    List<String> cimg = cinemadata.getThumb();
                    for (String img : cimg) {
                        imageList.add(img);
                    }
                    imageList.add(cinemadata.getQcode());

                }

            }
        }

        //下载京东图片
        ArrayList<Cinemadata> jddata = cranemaapi.getCinemajddata();
        for (Cinemadata cinemadata : jddata) {
            imageList.add(cinemadata.getImage_default_id());
            List<String> jdimg = cinemadata.getThumb();
            for (String img : jdimg) {
                imageList.add(img);
            }
            imageList.add(cinemadata.getQcode());
        }

        //下载卡券图片
        ArrayList<Cinemadata> kadata = cranemaapi.getCinemakadata();
        for (Cinemadata cinemadata : kadata) {
            imageList.add(cinemadata.getImage_default_id());
            List<String> kaimg = cinemadata.getThumb();
            for (String img : kaimg) {
                imageList.add(img);
            }
            imageList.add(cinemadata.getQcode());
        }
        handler.postDelayed(downRunnable, 10000);
    }

    /**
     * 等10秒再下载，避免阻塞加载网络
     */
    private Runnable downRunnable = new Runnable() {
        @Override
        public void run() {
            DownFileUtil downFileUtil = new DownFileUtil(imageList);
            downFileUtil.startDown();
        }
    };

    //解决singleInstance的Activity多次传入Intent的问题
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyTextWatch.refresh(this);
        cet_main_search.clearFocus();
        //每次进来先显示商品页面
        arl_main_bottom_content.setVisibility(View.VISIBLE);
        main_fragment_content.setVisibility(View.GONE);
        if (AddressAddFragment.Companion.getAddressAddDialog() != null) {
            AddressAddFragment.Companion.getAddressAddDialog().dismiss();
        }
        //判断是否需要显示登录页面
        if (null != getIntent() && !isUnLogin) {
            isUnLogin = getIntent().getBooleanExtra(IndexFuncitonActivity.Companion.isGoMemberCenter(), false);
        }

        //初始化
        if (isInit) {
            reinit();
        } else {
            isInit = true;
        }

        Constance.lastTouchTime = System.currentTimeMillis();
        handler.removeCallbacks(noTouchRunnable);
        handler.postDelayed(noTouchRunnable, 60 * 1000);

        //判断是否登录
        isLogining();

        //判断是否跳转到未登录界面
        if (isUnLogin) {
            isUnLogin = false;
            arl_main_bottom_content.setVisibility(View.GONE);
            main_fragment_content.setVisibility(View.VISIBLE);
        }

        //是否跳到结算界面(立即结算)
        if (isGoMall) {
            isGoMall = false;
            arl_main_bottom_content.setVisibility(View.GONE);
            main_fragment_content.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putString("fastbuy", "fastbuy");
            BalanceFragment balanceFragment = new BalanceFragment();
            balanceFragment.setArguments(bundle);
            manage.beginTransaction().replace(R.id.main_fragment_content, balanceFragment).commitAllowingStateLoss();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止扫描
        VbarUtils.getInstance(this).stopScan();
        countDownTimer.cancel();
        handler.removeCallbacks(noTouchRunnable);
        wx_handle.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        handler.removeCallbacksAndMessages(null);
        wx_handle.removeCallbacksAndMessages(null);
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    private void getSmallList() {
        helperPager = new SmallImagePager(getSupportFragmentManager(), cranemaapi.getCinemabigdata(), cranemaapi.getCinemasmalldata(), cranemaapi.getCinemajddata(), cranemaapi.getCinemakadata());
        viewPager.setAdapter(helperPager);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (TextView view : dotView) {
                    view.setTextColor(getResources().getColor(R.color.color_black));
                }
                dotView.get(position).setTextColor(getResources().getColor(R.color.colorAccent));
                pagerIndex = position + 1;
                if (pagerIndex >= dotView.size()) {
                    pagerIndex = 0;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });
    }

    private void updateVersion() {
        NetworkRequest.getInstance().getVersion(new NetworkCallback<Version>() {
            @Override
            public void onSuccess(Version data) {
                PackageManager pm = MallActivity.this.getPackageManager();
                PackageInfo pi = null;
                int versionCode = 0;
                try {
                    pi = pm.getPackageInfo(MallActivity.this.getPackageName(), 0);
                    versionCode = pi.versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (versionCode < data.getVersionid()) {
                    SharePerferenceUtil.getInstance().setValue(Constance.auto_Install, true);
                    vUpdateVersion.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    File file = new File(FileManager.getInstance().getDownFileDir() + "newapp" + data.getVersionid() + ".apk");
                    if (file.exists() && ((boolean) SharePerferenceUtil.getInstance().getValue("newapp" + data.getVersionid(), false))) {

                        AntoUtil.setUrl(FileManager.getInstance().getDownFileDir() + "newapp" +
                                data.getVersionid() + ".apk");
                        AntoUtil.install(MallActivity.this);

                        return;
                    }
                    DownFileUtil down = new DownFileUtil(null);
                    down.downApk(data.getUrl(), data.getVersionid(), MallActivity.this);
                } else {
//                    Toast.makeText(MallActivity.this,"已是最新版本",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //判断红包是否被点击
        if (inRangeOfView(ivRedPacket, ev) && ivRedPacket.isShown()) {
//            onClick(ivRedPacket);
            //跳转到商品详情
//            if (is_skip.equals("1")&&red_packet_cinema!=null&&ev.getAction()==MotionEvent.ACTION_UP){
//            Intent intent = new Intent(this, MaxImageActivity.class);
//            intent.putExtra("cinema", red_packet_cinema);
//            startActivity(intent);
//            }
            //显示定制页面
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                pcz_diaolog = new PredefineCustomizationDialog(MallActivity.this).create()
                        .setWebUrl(cranemaapi.getGiftCust(), cinemaType).show();
            }

        } else {
            ivRedPacket.setVisibility(View.GONE);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("uuu", "fff");
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 用户2分钟没操作执行
     */
    private Runnable noTouchRunnable = new Runnable() {
        @Override
        public void run() {
            if ((System.currentTimeMillis() - Constance.lastTouchTime) >= 60 * 1000) {
                //初始化
                reinit();

                String home_page = SharePerferenceUtil.getInstance().getValue("home_page", "1").toString();
                switch (home_page) {
                    //首页
                    case "1": {
                        //返回首页
                        startActivity(new Intent(MallActivity.this, HomePageActivity.class));
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
                        break;
                    }
                    //购币页面
                    case "2": {
                        //返回首页
                        startActivity(new Intent(MallActivity.this, BuyCoinsActivity.class));
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
                        break;
                    }
                    //商城页面
                    case "3": {
                        break;
                    }
                    default: {
                        //返回首页
                        startActivity(new Intent(MallActivity.this, HomePageActivity.class));
                        // 定义出入场动画
                        overridePendingTransition(R.anim.out_to_right_abit, R.anim.out_to_right);
                        break;
                    }
                }

            }
            handler.postDelayed(noTouchRunnable, 30 * 1000);
        }
    };

    /**
     * 判断是否点击到红包
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }


    /**
     * 获取红包图片接口
     */
    public void getRedPacket() {
        RequestParams requestParams = new RequestParams(Constance.HOST + Constance.RED_PACKET);
        requestParams.setAsJsonContent(true);
        requestParams.addHeader("charset", "utf-8");
        requestParams.addHeader("Content-Type", "application/json");
        requestParams.setConnectTimeout(10000);
        requestParams.addBodyParameter("shop_id", cinemaType);
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("getRedPacket" , " getRedPacket result = " + result);
                try {
                    JSONObject s = new JSONObject(result);
                    if ("0".equals(s.getString("errorcode"))) {
                        if ("1".equals(s.getJSONObject("data").get("type").toString())) {
                            Glide.with(getApplication())
                                    .load(s.getJSONObject("data").get("picture"))
                                    .placeholder(R.drawable.hand).into(ivRedPacket);

                            red_type = "1";
                        }
                        red_packet_cinema = GsonUtil.Companion.jsonToObject(s.getJSONObject("data").get("cinema").toString(), Cinemadata.class);
                        is_skip = s.getJSONObject("data").getString("is_skip");
                    } else {
//                        ivRedPacket.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
     * 初始化操作
     */
    private void reinit() {
        if (pcz_diaolog != null && pcz_diaolog.isShowing()) {
            pcz_diaolog.dismiss();
        }
        arl_main_bottom_content.setVisibility(View.VISIBLE);
        main_fragment_content.setVisibility(View.GONE);
        try {
            new tvOnClick(0).onClick(llDot);
            cet_main_search.setText("");
            ns_main_nice_spinner.setSelectedIndex(0);
            et_main_high_price.setText("");
            et_main_low_price.setText("");
            InputUtils.Companion.closeInput(MallActivity.this);
//            ivRedPacket.setVisibility(View.VISIBLE);
//         if ("1".equals(red_type)){
//             ivRedPacket.setVisibility(View.VISIBLE);
//         }else{
//             ivRedPacket.setVisibility(View.GONE);
//         }
        } catch (Exception e) {

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

                    isLogining();

                    //登录商城
//                    userLogin(Wid,Bid,Vpn);
                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(mContext).create()
                            .setMessage("登录成功!").show();

                } else {
                    ToastUtil.showToast(MallActivity.this, "登录失败");
                    startLoginRecognitionScan();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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
     * 会员卡登录通过ID
     * 更新会员信息需要注意是扫卡登录还是刷卡登录
     */
    public void scanCardLoginById() {
        MemberInfo memberInfo = GsonUtil.Companion.jsonToObject(SharePerferenceUtil.getInstance().getValue(Constance.member_Info, "").toString(), MemberInfo.class);

        HashMap<String, String> hashmap = new HashMap<String, String>();
        hashmap.put("CustID", memberInfo.getId());
        hashmap.put("sign", SignParamUtil.getSignStr(hashmap));
        KProgressHUD dia = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("请稍后...").show();
        HttpUtils.xPostJson(Constance.MEMBER_HOST + Constance.GetMemberInfoByCardNo, hashmap, new XutilsCallback<String>() {
            @Override
            public void onSuccessData(String result) {
                JsonObject jsonResult = GsonUtil.Companion.jsonToObject(result, JsonObject.class);
                if (jsonResult.has("return_Code") &&
                        jsonResult.get("return_Code").toString().equals("200") &&
                        jsonResult.getAsJsonObject("Data").get("Status").toString().equals("0")) {
                    JsonObject data = jsonResult.getAsJsonObject("Data");

                    MemberInfo memberInfo_new = GsonUtil.Companion.jsonToObject(data.toString(), MemberInfo.class);

                    //是否扫卡登录
                    memberInfo_new.setIsScan(memberInfo.getIsScan());

                    //临时保存会员信息
                    SharePerferenceUtil.getInstance()
                            .setValue(Constance.member_Info, GsonUtil.Companion.objectToJson(memberInfo_new));


                    //显示登录信息
                    isLogining();


                } else {
                    ToastUtil.showToast(MallActivity.this, "刷新失败,请重新登录查看最新信息!");
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
    private void isLogining() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {

            countDownTimer.cancel();
            //未登录状态
            arl_main_unlogin.setVisibility(View.VISIBLE);
            all_main_logining.setVisibility(View.GONE);

            //初始化信息
            tv_main_user_coins.setText("");
            tv_main_user_tickets.setText("");
            tv_main_username.setText("");

            LocalDefines.sIsLogin = false;
            mTvLoginPlease.setVisibility(View.VISIBLE);
            mTvVipName.setVisibility(View.INVISIBLE);
            mTvlottery.setVisibility(View.INVISIBLE);
            mTvLotteryCount.setVisibility(View.INVISIBLE);
            mTvGameCoin.setVisibility(View.INVISIBLE);
            mTvGameCoinCount.setVisibility(View.GONE);
            mBtnLogout.setVisibility(View.GONE);
//            mIvShoppingCart.setVisibility(View.GONE);
//            mBtnLogout.setBackgroundResource(R.drawable.shape_login);
//            mBtnLogout.setClickable(false);
            //显示Fragment为未登录页面
            manage.beginTransaction().replace(R.id.main_fragment_content, new MemberCenterFragment()).commitAllowingStateLoss();

            //设置购物车不能使用
//            btn_main_add_car.setEnabled(false);


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
            LocalDefines.sIsLogin = true;
            countDownTimer.cancel();
            //登录状态
            arl_main_unlogin.setVisibility(View.GONE);
            all_main_logining.setVisibility(View.VISIBLE);

            manage.beginTransaction().replace(R.id.main_fragment_content, new MenberCenterLoginingFragment()).commitAllowingStateLoss();

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
                mTvVipName.setText(loginJson.getCustName());
                mIvShoppingCart.setVisibility(View.VISIBLE);
                mTvLotteryCount.setText(loginJson.getTickets().substring(0, loginJson.getTickets().indexOf(".")));
                mTvGameCoinCount.setText(loginJson.getCoins().substring(0, loginJson.getCoins().indexOf(".")));
                try {
                    tv_main_user_coins.setText("游戏币:" + loginJson.getCoins().substring(0, loginJson.getCoins().indexOf(".")));
                    tv_main_user_tickets.setText("彩票数:" + loginJson.getTickets().substring(0, loginJson.getTickets().indexOf(".")));

                } catch (Exception e) {
                    tv_main_user_coins.setText("游戏币:" + loginJson.getCoins());
                    tv_main_user_tickets.setText("彩票数:" + loginJson.getTickets());
                }

                tv_main_username.setText(loginJson.getCustName());

            }


            countDownTimer.start();
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
     * 居中提示框
     */
    public void CToast(String message) {
        synchronized (mContext) {
            toast.cancel();
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout layout = (LinearLayout) toast.getView();
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextSize(AutoUtils.getPercentHeightSize(80));
            toast.show();
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
                    manage.beginTransaction().replace(R.id.main_fragment_content, new MemberCenterFragment()).commitAllowingStateLoss();


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
                    isLogining();

                    //登录商城
//                    userLogin(Wid,Bid,Vpn);
                    if (!TextUtils.isEmpty(Bid)
                            && !TextUtils.isEmpty(Vpn)) {
                        //执行商城会员登录
                        userLogin(Wid, Bid, Vpn);
                    }

                    //登录提示
                    new TisDialog(mContext).create()
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


    /**
     * 是否已经登录商城
     */
    private boolean isAbleShop() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {

            //每次进来先显示商品页面
            arl_main_bottom_content.setVisibility(View.GONE);
            main_fragment_content.setVisibility(View.VISIBLE);
            //显示Fragment为未登录页面
            manage.beginTransaction().replace(R.id.main_fragment_content, new MemberCenterFragment()).commitAllowingStateLoss();

            CToast("请登录");
            return false;
        } else if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.user_accessToken, "").toString())) {

            CToast("请关注公众号绑卡或到前台添加手机号码,再重新登录!");
            return false;
        } else {
            return true;
        }

    }

}
