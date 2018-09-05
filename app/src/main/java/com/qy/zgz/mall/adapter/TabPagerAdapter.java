package com.qy.zgz.mall.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qy.zgz.mall.R;

import java.util.ArrayList;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<Fragment> mFragmentList;
    private String[] mTitleArray = {"待付款", "待发货" ,"待收货", "待评价", "全部"};

    public TabPagerAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.mContext = context;
        this.mFragmentList = fragmentList;
    }

//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }

    @Override
    public int getCount() {
        return mTitleArray.length;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
////        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_aaa_test, container, false);
//        TextView tv = new TextView(mContext);
//        tv.setText(mTitleArray[position]);
//        tv.setGravity(Gravity.CENTER);
////        tv.setTextSize(70);
//        container.addView(tv, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
//        return tv;
//    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleArray[position];
    }
}
