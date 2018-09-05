package com.qy.zgz.mall.page.fragment;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qy.zgz.mall.BaseFragment;
import com.qy.zgz.mall.Model.MemberInfo;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.adapter.TabPagerAdapter;
import com.qy.zgz.mall.network.Constance;
import com.qy.zgz.mall.page.index.VIPCenterActivity;
import com.qy.zgz.mall.utils.GsonUtil;
import com.qy.zgz.mall.utils.LocalDefines;
import com.qy.zgz.mall.utils.SharePerferenceUtil;
import com.qy.zgz.mall.utils.ToastUtil;
import com.qy.zgz.mall.vbar.VbarUtils;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class VipCenterInfoFragment extends BaseFragment {

    @BindView(R.id.tl_tab)
    TabLayout mTabLayout;
    @BindView(R.id.vp_pager)
    ViewPager mViewPager;

    @BindView(R.id.rl_pending_payment)
    AutoRelativeLayout mRlPendingPayment;
    @BindView(R.id.rl_pending_delivery)
    AutoRelativeLayout mRlPendingDelivery;
    @BindView(R.id.rl_pending_receiver)
    AutoRelativeLayout mRlPendingReceiver;
    @BindView(R.id.rl_pending_evalution)
    AutoRelativeLayout mRlPendingEvalution;
    @BindView(R.id.rl_my_order)
    AutoRelativeLayout mRlMyOrder;
    @BindView(R.id.iv_hide_viewpager)
    ImageView mIvHideViewPager;
    @BindView(R.id.rl_check_all_order)
    AutoRelativeLayout mRlCheckAllOrder;
    @BindView(R.id.ll_order_info)
    AutoLinearLayout mLlOrderInfo;

    /*-- 会员中心信息栏--*/
    @BindView(R.id.tv_vip_name_title)
    TextView mTvVipNameTitle;
    @BindView(R.id.tv_vip_number)
    TextView mTvVipNumber;
    @BindView(R.id.tv_vip_level)
    TextView mTvVipLevel;
    @BindView(R.id.tv_lottery_count_title)
    TextView mTvLotteryCountTitle;
    @BindView(R.id.tv_game_coin_count_title)
    TextView mTvGameCoinCountTitle;
    @BindView(R.id.tv_point_count)
    TextView mTvPointCount;
    @BindView(R.id.tv_replacement_coin_count)
    TextView mTvReplacementCoinTitle;
    @BindView(R.id.tv_deposit_count)
    TextView mTvDeposit;
    @BindView(R.id.ll_vip_info_layout)
    AutoLinearLayout mLlVipInfoLayout;

    VIPCenterActivity mActivity;
    private TabPagerAdapter mViewPageAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (VIPCenterActivity) context;
    }

    @Override
    public View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_vip_info, container, false);
        return view;
    }

    @Override
    public void initViews(View view) {
//        countDownTimer = new LoginCountDownTimer(this, 30000, 1000);
        initViewPager();
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoginInfo();
    }

    @Override
    public void onPause() {
        super.onPause();
        VbarUtils.getInstance(mActivity).stopScan();
        mBaseFragmentHandler.removeCallbacksAndMessages(null);
    }

    private void initViewPager() {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
        fragmentArrayList.add(PaymentFragment.newInstance());
        fragmentArrayList.add(DeliveryFragment.newInstance());
        fragmentArrayList.add(ReceiverFragment.newInstance());
        fragmentArrayList.add(EvalutionFragment.newInstance());
        fragmentArrayList.add(AllOrderFragment.newInstance());
        /*  fragment中使用必须使用getChildFragmentManager() */
        mViewPageAdapter = new TabPagerAdapter(mActivity, getChildFragmentManager(), fragmentArrayList);
        mViewPager.setAdapter(mViewPageAdapter);
//        mTabLayout.setTabMode(TabLayout.MODE_FIXED);    // 默认模式，可以不设置);
        mTabLayout.setupWithViewPager(mViewPager);//给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
    }

    private void showViewPager() {
        if (LocalDefines.sIsLogin) {
            mRlPendingPayment.setVisibility(View.GONE);
            mRlPendingDelivery.setVisibility(View.GONE);
            mRlPendingReceiver.setVisibility(View.GONE);
            mRlPendingEvalution.setVisibility(View.GONE);
            mLlOrderInfo.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            mRlMyOrder.setVisibility(View.VISIBLE);
            mLlVipInfoLayout.setVisibility(View.GONE);
        } else {
            ToastUtil.showToast(mActivity, "请先登录");
        }
    }

    private void hideViewPager() {
        mRlPendingPayment.setVisibility(View.VISIBLE);
        mRlPendingDelivery.setVisibility(View.VISIBLE);
        mRlPendingReceiver.setVisibility(View.VISIBLE);
        mRlPendingEvalution.setVisibility(View.VISIBLE);
        mLlOrderInfo.setVisibility(View.VISIBLE);
        mRlMyOrder.setVisibility(View.GONE);
        mTabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        mLlVipInfoLayout.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.rl_pending_payment, R.id.rl_pending_delivery, R.id.rl_pending_receiver,
            R.id.rl_pending_evalution, R.id.iv_hide_viewpager, R.id.rl_check_all_order})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_pending_payment:
                showViewPager();
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.rl_pending_delivery:
                showViewPager();
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.rl_pending_receiver:
                showViewPager();
                mViewPager.setCurrentItem(2, true);
                break;
            case R.id.rl_pending_evalution:
                showViewPager();
                mViewPager.setCurrentItem(3, true);
                break;
            case R.id.iv_hide_viewpager:
                hideViewPager();
                break;
            case R.id.rl_check_all_order:
                showViewPager();
                mViewPager.setCurrentItem(4, true);
                break;
            default:
                break;
        }
    }

    public void showLoginInfo() {
        if (TextUtils.isEmpty(SharePerferenceUtil.getInstance()
                .getValue(Constance.member_Info, "").toString())) {
            //未登录状态
            mBaseFragmentHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //开启登录识别扫描器
//                    startLoginRecognitionScan();
                    //创建新的微信授权二维码
//                    CreateScanCode(SharePerferenceUtil.getInstance()
//                            .getValue(Constance.MachineID, "").toString());
                }
            }, 500);

            mTvVipNameTitle.setText("会员名称：--/--");
            mTvVipNumber.setText("会员编号：--/--");
            mTvVipLevel.setText("会员等级：--/--");
            mTvLotteryCountTitle.setText("--/--");
            mTvGameCoinCountTitle.setText("--/--");
            mTvPointCount.setText("--/--");
            mTvReplacementCoinTitle.setText("--/--");
            mTvDeposit.setText("--/--");


        } else {
            //初始化登录信息
            String logininfo = SharePerferenceUtil.getInstance()
                    .getValue(Constance.member_Info, "").toString();
            MemberInfo loginJson = GsonUtil.Companion.jsonToObject(logininfo, MemberInfo.class);

            if (loginJson != null) {
                mTvLotteryCountTitle.setText(loginJson.getTickets().substring(0, loginJson.getTickets().indexOf(".")));
                mTvGameCoinCountTitle.setText(loginJson.getCoins().substring(0, loginJson.getCoins2().indexOf(".")));
                mTvVipNameTitle.setText("会员名称：" + loginJson.getCustName());
                mTvVipNumber.setText("会员编号：" + loginJson.getNumber());
                mTvVipLevel.setText("会员等级：" + loginJson.getLevelName());

                mTvPointCount.setText(loginJson.getPoint().substring(0, loginJson.getPoint().indexOf(".")));
                mTvReplacementCoinTitle.setText(loginJson.getDeposit().substring(0, loginJson.getDeposit().indexOf(".")));
                mTvDeposit.setText(loginJson.getMoney().substring(0, loginJson.getMoney().indexOf(".")));
            }
        }
    }
}
