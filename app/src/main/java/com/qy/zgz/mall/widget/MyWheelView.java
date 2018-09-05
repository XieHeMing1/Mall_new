package com.qy.zgz.mall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.contrarywind.view.WheelView;

import butterknife.OnTouch;

/**
 * Created by ZYB on 2018/5/16 0016.
 */

public class MyWheelView extends WheelView {

    private OnWheelTouchListener listener;

    public MyWheelView(Context context) {
        super(context);
    }

    public MyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener!=null)
            listener.onTouch();
        return super.onTouchEvent(event);
    }

    public OnWheelTouchListener getListener() {
        return listener;
    }

    public void setListener(OnWheelTouchListener listener) {
        this.listener = listener;
    }

    public interface OnWheelTouchListener{
        void onTouch();
    }
}
