package com.qy.zgz.mall.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qy.zgz.mall.Model.Prize;
import com.qy.zgz.mall.R;
import com.qy.zgz.mall.utils.FileManager;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * 老虎机item
 */
public class SlotMachinView extends AutoRelativeLayout {
    private final String CASH="cash";       //现金
    private final String GOODS="goods";     //商品
    private final String TICKET="ticket";   //电影票
    private final String COUPONS="coupons"; //优惠券
    private SimpleDraweeView ivImage;
    private TextView tvNum;
    public SlotMachinView(Context context) {
        super(context);

    }

    public SlotMachinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.layout_slot_machin, this);

        ivImage=this.findViewById(R.id.iv_image);
        tvNum=this.findViewById(R.id.tv_num);
        tvNum.setVisibility(INVISIBLE);
    }

    public void showData(Prize prize)
    {
        this.setEnabled(true);
        AutoRelativeLayout.LayoutParams layoutParams= null;
        Uri uri=null;
        String fileName = FileManager.getInstance().getFileName(prize.getImg());
        if (FileManager.getInstance().isFileExists(fileName)){
            uri=Uri.parse("file://" + FileManager.getInstance().getDestFileDir() + fileName);
        }else{
            uri=Uri.parse(prize.getImg());
        }

//        switch (prize.getType())
//        {
//            case CASH:
//                uri=Uri.parse("res://" + MyApplication.getInstance().getPackageName() + "/" + R.drawable.ic_cash);
//                layoutParams= (LayoutParams) tvNum.getLayoutParams();
//                layoutParams.setMargins(0, AutoUtils.getPercentWidthSize(215), AutoUtils.getPercentWidthSize(165),0);
//                tvNum.setLayoutParams(layoutParams);
//                tvNum.setText(prize.getMoney());
//                break;
//            case GOODS:
//                uri=Uri.parse(prize.getImg());
//                break;
//            case TICKET:
//                uri=Uri.parse("res://" + MyApplication.getInstance().getPackageName() + "/" + R.drawable.ic_cinema_ticket);
//                layoutParams= (LayoutParams) tvNum.getLayoutParams();
//                layoutParams.setMargins(0, AutoUtils.getPercentWidthSize(205), AutoUtils.getPercentWidthSize(70),0);
//                tvNum.setLayoutParams(layoutParams);
//                tvNum.setText(prize.getNum());
//                break;
//            case COUPONS:
//                uri=Uri.parse("res://" + MyApplication.getInstance().getPackageName() + "/" + R.drawable.ic_coupon);
//                layoutParams= (LayoutParams) tvNum.getLayoutParams();
//                layoutParams.setMargins(0, AutoUtils.getPercentWidthSize(195), AutoUtils.getPercentWidthSize(180),0);
//                tvNum.setLayoutParams(layoutParams);
//                tvNum.setText(prize.getMoney());
//                break;
//        }
        try {
            ivImage.setImageURI(uri);
        }catch (Exception e){

        }


    }

}
