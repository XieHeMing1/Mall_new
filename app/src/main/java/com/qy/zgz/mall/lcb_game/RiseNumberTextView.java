package com.qy.zgz.mall.lcb_game;

/**
 * Created by LCB on 2018/8/16.
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.qy.zgz.mall.R;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 数字动画自定义
 */
@SuppressLint("AppCompatCustomView")
public class RiseNumberTextView extends AutoRelativeLayout implements RiseNumberBase {
    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private int mPlayingState = STOPPED;
    private float number;
    private float fromNumber;
    private long duration = 1000;
    /**
     * 1.int 2.float
     */
    private int numberType = 2;
    private boolean flags = true;
    private EndListener mEndListener = null;
    private TimeTickListener timeTickListener = null;
    final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };

    private TextView rn_game_time;
    private TextView rn_game_time_bg;
    public RiseNumberTextView(Context context) {
        super(context);
        init(context);
    }
    public RiseNumberTextView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }
    public RiseNumberTextView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }
    public interface EndListener {
        public void onEndFinish();
    }
    public interface TimeTickListener {
        public void TimeTick();
    }
    public boolean isRunning() {
        return (mPlayingState == RUNNING);
    }

    private void init(Context context){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.rise_number, this);
        rn_game_time=(TextView) findViewById(R.id.rn_game_time);
        rn_game_time_bg=(TextView) findViewById(R.id.rn_game_time_bg);
        // 准备字体
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "digital2" + File.separator
                + "Digital2.ttf");
        // 设置字体
        rn_game_time.setTypeface(typeface);
        rn_game_time_bg.setTypeface(typeface);

    }

    private void runFloat() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromNumber, number);
//        valueAnimator.setDuration(duration);
        valueAnimator.setDuration((int)(number*1000));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (timeTickListener!=null) {
                    timeTickListener.TimeTick();
                }
                if (flags) {
                    rn_game_time.setText(formaText(",##00.00").format(Double.parseDouble(valueAnimator.getAnimatedValue().toString())) + "");
                    if (valueAnimator.getAnimatedValue().toString().equalsIgnoreCase(number + "")) {
                        rn_game_time.setText(formaText(",##0.00").format(Double.parseDouble(number + "")));
                    }
                } else {
                    rn_game_time.setText(formaText("##00.00").format(Double.parseDouble(valueAnimator.getAnimatedValue().toString())) + "");
                    if (valueAnimator.getAnimatedValue().toString().equalsIgnoreCase(number + "")) {
                        rn_game_time.setText(formaText("##00.00").format(Double.parseDouble(number + "")));
                    }
                }
                if (valueAnimator.getAnimatedFraction() >= 1 ||  mPlayingState == STOPPED) {
                    mPlayingState = STOPPED;
                    valueAnimator.cancel();
                    if (mEndListener != null){
                        mEndListener.onEndFinish();
                    }
                }
            }
        });
        valueAnimator.start();


    }


    static int sizeOfInt(int x) {
        for (int i = 0;; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    @Override
    public void start() {
        if (!isRunning()) {
            mPlayingState = RUNNING;
            startTimeLong=0L;
            runInt();
//            if (numberType == 1){
//                runInt();
//            }
//            else{
//                runFloat();
//
//                }
        }
    }
    @Override
    public RiseNumberTextView withNumber(float number, boolean flag) {
        this.number = number;
        this.flags = flag;
        numberType = 2;
        fromNumber = 0;
        return this;
    }
    @Override
    public RiseNumberTextView withNumber(float number) {
        System.out.println(number);
        this.number = number;
        numberType = 2;
        fromNumber = 0;
        return this;
    }
    @Override
    public RiseNumberTextView withNumber(int number) {
        this.number = number;
        numberType = 1;
        fromNumber = 0;
        return this;
    }
    @Override
    public RiseNumberTextView setDuration(long duration) {
        this.duration = duration;
        return this;
    }
    @Override
    public void setOnEnd(EndListener callback) {
        mEndListener = callback;
    }

    @Override
    public void setOnTimeTick(TimeTickListener callback) {
        timeTickListener=callback;
    }


    CountDownTimer countDownTimer = null;
    ValueAnimator  valueAnimatorInt;
    private void runInt() {
//        if (timeTickListener!=null){
//            countDownTimer=new CountDownTimer((int)(number*10), 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    Log.e("ww",showIntText((int)(number*10-millisUntilFinished)/10));
//                    rn_game_time.setText(showIntText((int)(number*10-millisUntilFinished)/10));
//                    timeTickListener.TimeTick();
//                }
//
//                @Override
//                public void onFinish() {
//                    mPlayingState = STOPPED;
//                    if (mEndListener != null){
//                        mEndListener.onEndFinish();
//                    }
//                }
//            };
//        }
//        if (countDownTimer!=null){
//            countDownTimer.start();
//        }

          valueAnimatorInt = ValueAnimator.ofInt((int) fromNumber, (int) number);
//        valueAnimator.setDuration(duration);
        valueAnimatorInt.setDuration(10000+new Random().nextInt(3001));
        valueAnimatorInt.addUpdateListener(listenerInt);
        valueAnimatorInt.addListener(new ValueAnimator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPlayingState = STOPPED;
                lastIntValue="";
                if (mEndListener != null){
                    mEndListener.onEndFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e("wd",valueAnimatorInt.getAnimatedValue()+"");
                if (timeTickListener!=null) {
                    timeTickListener.TimeTick();
                }
                rn_game_time.setText(showIntText(Integer.parseInt(valueAnimatorInt.getAnimatedValue().toString())));
                if (mEndListener != null){
                    mEndListener.onEndFinish();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimatorInt.start();
    }

    public void stop (){
        if (isRunning()) {
            mPlayingState = STOPPED;
            lastIntValue="";
//            countDownTimer.cancel();
//            if (mEndListener != null){
//                mEndListener.onEndFinish();
//            }
            valueAnimatorInt.cancel();


        }
    }


    //格式化
    public DecimalFormat formaText(String pattern) {
        DecimalFormat dfs = new DecimalFormat();
        dfs.setRoundingMode(RoundingMode.FLOOR);
        dfs.applyPattern(pattern);
        return dfs;
    }

    //格式化
    public String showIntText(int pattern) {
        if (pattern<0){
            pattern=0;
        }
        String result="";
        int front=pattern/100;
        int back=pattern%100;
        if (front<10){
            result+="0"+pattern/100+":";
        }else{
            result+=pattern/100+":";
        }
        if (back<10){
            result+="0"+pattern%100;
        }else{
            result+=pattern%100;
        }


        return result;
    }

    //记录最后一个整形值
    String lastIntValue="";
    ValueAnimator.AnimatorUpdateListener listenerInt=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            String curryValue=valueAnimator.getAnimatedValue().toString();
            if (lastIntValue.equals(curryValue)){
                return;
            }
            lastIntValue=curryValue;
            Log.e("w",curryValue);
            if (timeTickListener!=null) {
                timeTickListener.TimeTick();
            }
            rn_game_time.setText(showIntText(Integer.parseInt(curryValue)));

        }
    };


    //----------使用HANDLE计数------------------------------
    private Long startTimeLong=0L;

    public  String getCurrySeconds(){
        Date date=new Date(startTimeLong);
        SimpleDateFormat format=new SimpleDateFormat("ss:SS");
        String result=format.format(date);
        if (result.length()>5){
            result=result.substring(0,result.length()-1);
        }

        return result;
    }


}