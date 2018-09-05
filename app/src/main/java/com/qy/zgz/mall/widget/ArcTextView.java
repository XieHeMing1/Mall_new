package com.qy.zgz.mall.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2018/2/28 0028.
 */

public class ArcTextView extends View {
    //内容画笔
    private Paint mPaintText;
    public ArcTextView(Context context) {
        super(context);
    }

    public ArcTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArcTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        //内容
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setFlags(Paint.ANTI_ALIAS_FLAG);
    }
    private List<String>  mText;
    private int mRadius = 20;
    private int mColor;
    private int mTextSize;
    public void setText(List<String> text, int textSize, int radius, int color) {
        int mDensity = (int) getResources().getDisplayMetrics().density;
        mText = text;
        mColor = getResources().getColor(color);
        mTextSize = textSize;
        mRadius = radius;
        mPaintText.setColor(mColor);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setStyle(Paint.Style.FILL);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTextView(canvas);
    }

    private Path mPath;

    private void drawTextView(Canvas canvas) {
        if (mPath == null) {
            mPath = new Path();
            RectF rectF = new RectF(0, 0, mRadius * 2, mRadius * 2);
            mPath.addOval(rectF, Path.Direction.CCW);
        }
        double angle=0;
        for(int i=0;i<mText.size();i++)
        {
            String text=mText.get(i);
            angle=draw(text,canvas, (int) (45*i));
        }
    }

    private double draw(String text,Canvas canvas,int rotate)
    {
        Rect rectText = new Rect();
        mPaintText.getTextBounds(text, 0, text.length(), rectText);
        int textWidth = rectText.width();
        int textWidthHalf = textWidth / 2;
        double hudu = Math.asin(textWidthHalf / (mRadius * 1.0f));
        double angle = hudu * 180 / Math.PI;
        Log.d("ArcTextView", "-->angle:" + angle + "--hudu:" + hudu);
        canvas.rotate((float) (rotate + angle), getWidth() / 2, getHeight() / 2);
        canvas.drawTextOnPath(text, mPath, 0, -10, mPaintText);
        canvas.rotate((float)(360-rotate-angle),getWidth()/2,getHeight()/2);
        return angle;
    }
}
