package com.qy.zgz.mall.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.Model.Cinemadata;
import com.qy.zgz.mall.MyApplication;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.page.max.MaxImageActivity;
import com.qy.zgz.mall.utils.FileManager;
import com.qy.zgz.mall.utils.FrescoUtils;
import com.qy.zgz.mall.utils.InputUtils;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * 小图holder
 */
public class SmallImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private SimpleDraweeView ivItem;
    private TextView tv_smallimg_name;
    private TextView tv_smallimg_price;
    private TextView tv_smallimg_price_now;
    private AutoRelativeLayout arl_item_image_price;
    private Cinemadata cinemadata;
    private ImageView iv_item_image_shopcar;
    public SmallImageViewHolder(View itemView) {
        super(itemView);
        AutoUtils.autoSize(itemView);
        ivItem=(SimpleDraweeView)itemView.findViewById(R.id.iv_item);
        ivItem.setOnClickListener(this);
        tv_smallimg_name=(TextView)itemView.findViewById(R.id.tv_smallimg_name);
        tv_smallimg_price=(TextView)itemView.findViewById(R.id.tv_smallimg_price);
        tv_smallimg_price_now=(TextView)itemView.findViewById(R.id.tv_smallimg_price_now);
        iv_item_image_shopcar=(ImageView)itemView.findViewById(R.id.iv_item_image_shopcar);
        arl_item_image_price=(AutoRelativeLayout)itemView.findViewById(R.id.arl_item_image_price);
        //跟点击图片跳转的内容一样
        iv_item_image_shopcar.setOnClickListener(this);

        tv_smallimg_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtils.Companion.closeInput(itemView.getContext());
            }
        });

        arl_item_image_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtils.Companion.closeInput(itemView.getContext());
            }
        });
    }

    public void showData(Cinemadata cinemadata)
    {
        this.cinemadata=cinemadata;
        if(cinemadata.getThumb()==null)
        {
            return;
        }
        String fileName= FileManager.getInstance().getFileName(cinemadata.getImage_default_id());
        Uri uri=null;
        if(FileManager.getInstance().isFileExists(fileName))
        {
            uri=Uri.parse("file://" + FileManager.getInstance().getDestFileDir() + fileName);
        }
        else
        {
            uri=Uri.parse(cinemadata.getImage_default_id());
        }

        FrescoUtils.showThumb(ivItem,uri,AutoUtils.getPercentWidthSize(800),AutoUtils.getPercentWidthSize(800));

        //设置圆角半径
        ivItem.setHierarchy(new GenericDraweeHierarchyBuilder(itemView.getContext().getResources()).
                setRoundingParams(RoundingParams.fromCornersRadius(10)).build());

        tv_smallimg_name.setText(cinemadata.getTitle());

        if ("1".equals(cinemadata.getHolidaytag())){
            tv_smallimg_name.setTextColor(itemView.getContext().getResources().getColor(R.color.color_red));
            tv_smallimg_price_now.setVisibility(View.VISIBLE);
            tv_smallimg_price.setText(cinemadata.getOldtickets());//显示原价
            tv_smallimg_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            tv_smallimg_price_now.setText(cinemadata.gettickets());//显示现价
        }else{
            tv_smallimg_name.setTextColor(itemView.getContext().getResources().getColor(R.color.color_black));
            tv_smallimg_price_now.setVisibility(View.GONE);
            tv_smallimg_price.setText(cinemadata.gettickets());//显示现价
            tv_smallimg_price.getPaint().setFlags(0);  // 取消设置的的划线
        }

    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(MyApplication.getInstance(),MaxImageActivity.class);
        intent.putExtra("cinema",cinemadata);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getInstance().startActivity(intent);
    }
}
