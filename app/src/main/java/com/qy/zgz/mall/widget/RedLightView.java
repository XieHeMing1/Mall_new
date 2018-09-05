package com.qy.zgz.mall.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.qy.zgz.mall.R;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * 小图自定义view
 */
public class RedLightView extends AutoRelativeLayout {
    private ImageView ivLight;
    public RedLightView(Context context) {
        super(context);

    }

    public RedLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.layout_light, this);

        ivLight=this.findViewById(R.id.iv_light);
        AnimationDrawable frameAnim=(AnimationDrawable) getResources().getDrawable(R.drawable.an_red_light);
        ivLight.setBackground(frameAnim);
        frameAnim.start();
    }


}
