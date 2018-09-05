package com.qy.zgz.mall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.qy.zgz.mall.R;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * 老虎机item
 */
public class NumberView extends AutoRelativeLayout {
    private ImageView ivNumber1;
    private ImageView ivNumber2;
    private ImageView ivNumber3;
    private ImageView ivNumber4;
    private ImageView ivNumber5;
    private ImageView ivNumber6;
    private int[] numberId = new int[]{R.drawable.ic_number_0, R.drawable.ic_number_1, R.drawable.ic_number_2,
            R.drawable.ic_number_3, R.drawable.ic_number_4, R.drawable.ic_number_5, R.drawable.ic_number_6,
            R.drawable.ic_number_7, R.drawable.ic_number_8, R.drawable.ic_number_9,R.drawable.ic_number_gray};
    private int number=-1;
    public NumberView(Context context) {
        super(context);

    }

    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.layout_number, this);
        ivNumber1 = findViewById(R.id.iv_num1);
        ivNumber2 = findViewById(R.id.iv_num2);
        ivNumber3 = findViewById(R.id.iv_num3);
        ivNumber4 = findViewById(R.id.iv_num4);
        ivNumber5 = findViewById(R.id.iv_num5);
        ivNumber6 = findViewById(R.id.iv_num6);
    }

    public void showNumber(int number) {
        if(this.number==number)
        {
            return;
        }
        this.number=number;
        int num1=10;
        int num2=10;
        int num3=10;
        int num4=10;
        int num5=10;
        int num6=10;
        if (number > 999999) {
             num1=9;
             num2=9;
             num3=9;
             num4=9;
             num5=9;
             num6=9;
        } else {
            if (number > 99999) {
                num1 = number / 100000;
            }
            if(number>9999)
            {
                num2=number %100000 /10000;
            }
            if(number>999)
            {
                num3=number %10000 /1000;
            }
            if(number>99)
            {
                num4=number %1000 /100;
            }
            if(number>9)
            {
                num5=number %100/10;
            }
            num6=number%10;
        }
        ivNumber1.setBackgroundResource(numberId[num1]);
        ivNumber2.setBackgroundResource(numberId[num2]);
        ivNumber3.setBackgroundResource(numberId[num3]);
        ivNumber4.setBackgroundResource(numberId[num4]);
        ivNumber5.setBackgroundResource(numberId[num5]);
        ivNumber6.setBackgroundResource(numberId[num6]);
    }

    public int getNumber(){
        return this.number;
    }
}
