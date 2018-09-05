package com.qy.zgz.mall.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qy.zgz.mall.Model.Cinemadata;
import com.qy.zgz.mall.Model.CinemadataCategory;
import com.qy.zgz.mall.page.fragment.SmallImageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 小图fragment的pager
 */
public class SmallImagePager extends FragmentPagerAdapter {

    private List<Fragment> fragmentList=new ArrayList<>();
    public SmallImagePager(FragmentManager fm, List<Cinemadata> mediuList, List<CinemadataCategory> smallList,List<Cinemadata> jdlist,List<Cinemadata> kjlist)
    {
        super(fm);
        if (null==mediuList){
            mediuList=new ArrayList<Cinemadata>();
        }
        if (null==smallList){
            smallList=new ArrayList<CinemadataCategory>();
        }
        if (null==jdlist){
            jdlist=new ArrayList<Cinemadata>();
        }
        if (null==kjlist){
            kjlist=new ArrayList<Cinemadata>();
        }

        int smallListSize=smallList.size();
        for(int i=0;i<smallListSize+3;i++)
        {
            SmallImageFragment smallImageFragment=new SmallImageFragment();
            //热门图片数据
            if (i==0){
            smallImageFragment.setImageList(mediuList,mediuList);
            }
            //京东图片数据
            else if (i==smallList.size()+1){
                smallImageFragment.setImageList(jdlist,jdlist);
            }
            //卡券图片数据
            else if (i==smallList.size()+2){
                smallImageFragment.setImageList(kjlist,kjlist);
            }
            else {
                smallImageFragment.setImageList(mediuList,smallList.get(i-1).category_data);
            }
            fragmentList.add(smallImageFragment);
        }
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
