package com.qy.zgz.mall.slot_machines;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.base.BaseRxActivity;
import com.qy.zgz.mall.utils.MyAnimationUtils;
import com.qy.zgz.mall.widget.ArcTextView;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/2/28 0028.
 */

public class SlotActivity extends BaseRxActivity {
    @BindView(R.id.rl_slot)
    AutoRelativeLayout rlSlot;
    @BindView(R.id.iv1)
    SimpleDraweeView iv1;

    @BindView(R.id.iv2)
    SimpleDraweeView iv2;

    @BindView(R.id.iv3)
    SimpleDraweeView iv3;

    @BindView(R.id.iv4)
    SimpleDraweeView iv4;

    @BindView(R.id.iv5)
    SimpleDraweeView iv5;

    @BindView(R.id.iv6)
    SimpleDraweeView iv6;

    @BindView(R.id.iv7)
    SimpleDraweeView iv7;

    @BindView(R.id.iv8)
    SimpleDraweeView iv8;

    @BindView(R.id.tv1)
    ArcTextView tv1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_slot2;
    }

    @Override
    protected void initView() {
        MyAnimationUtils.getInstance().rotation(iv1,0f,-90f,0,0,0,null);
        MyAnimationUtils.getInstance().rotation(iv3,0f,90f,0,0,0,null);
        MyAnimationUtils.getInstance().rotation(iv4,0f,180f,0,0,0,null);
        MyAnimationUtils.getInstance().rotation(iv5,0f,-45f,0,0,0,null);
        MyAnimationUtils.getInstance().rotation(iv6,0f,45f,0,0,0,null);
        MyAnimationUtils.getInstance().rotation(iv7,0f,-135f,0,0,0,null);
        MyAnimationUtils.getInstance().rotation(iv8,0f,135f,0,0,0,null);
        iv1.setImageURI(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1519381860213&di=7de82214406e759ec3f1cbe3e3a99a92&imgtype=0&src=http%3A%2F%2Fimg5.xiazaizhijia.com%2Fwalls%2F20160108%2F1024x768_d33e81709cb5f3b.jpg"));
        iv2.setImageURI(Uri.parse("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3182180566,1169717518&fm=27&gp=0.jpg"));
        iv3.setImageURI(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1519381930013&di=615d7c241473450796b59b536591aa05&imgtype=0&src=http%3A%2F%2Fpic9.nipic.com%2F20100819%2F5390059_214000042308_2.jpg"));
        iv4.setImageURI(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1519381904621&di=963ef0a4db75ab7f21effc11fd7ab7ce&imgtype=0&src=http%3A%2F%2Fimage.tianjimedia.com%2FuploadImages%2F2015%2F209%2F15%2FXETN9AOZ51OA.jpg"));
        iv5.setImageURI(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1519381886909&di=18fa95315032e7e46762cc3913428685&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dpixel_huitu%252C0%252C0%252C294%252C40%2Fsign%3Decfe83b9042442a7ba03f5e5b83bc827%2F728da9773912b31bc2fe74138d18367adab4e17e.jpg"));
        iv6.setImageURI(Uri.parse("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2153437082,2266466634&fm=27&gp=0.jpg"));
        iv7.setImageURI(Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1519795643917&di=d747e7323666b7b94b4ec5f6b5258f06&imgtype=0&src=http%3A%2F%2Fwww.ipxuo.cn%2Ffile%2Fupload%2F201606%2F14%2F150922151647794.jpg"));
        iv8.setImageURI(Uri.parse("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2959967830,3520971701&fm=27&gp=0.jpg"));
        List<String> list=new ArrayList<>();
        list.add("5元代金券");
        list.add("IphoneX 64G");
        list.add("电影票X3");
        list.add("5元现金");
        list.add("10元现金");
        list.add("优惠券");
        list.add("毛绒娃娃");
        list.add("芭比娃娃");
        tv1.setText(list, AutoUtils.getPercentWidthSize(80), AutoUtils.getPercentWidthSize(700),R.color.font_black);
     }

    @OnClick(R.id.iv_start)
    public void onClick(View v)
    {
        MyAnimationUtils.getInstance().rotation(rlSlot, 0f, 360f, 100, 0, 10, new MyAnimationUtils.AddListener() {
            @Override
            public void end() {
                Random random=new Random();
                int rotation=random.nextInt(8);
                rotation=rotation*45;
                MyAnimationUtils.getInstance().rotation(rlSlot,0f,rotation,500,0,0,null);
            }
        });
    }
}
