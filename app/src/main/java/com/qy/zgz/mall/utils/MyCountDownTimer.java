package com.qy.zgz.mall.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by LCB on 2018/5/10.
 */

public abstract class MyCountDownTimer {

    private final long mMillisInFuture; //倒计时的总时间
    private final long mCountdownInterval; //倒计时的间隔时间
    private long mStopTimeInFuture;
    public boolean mCancelled = false; //是否取消计时任务

    private MyCountDownTimer myCountDownTimer=this;
    //构造方法
    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    //取消倒计时
    public synchronized final void cancel() {
        Log.e("nn","nn");
        mCancelled = true;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeMessages(MSG);

    }

    //开始倒计时
    public synchronized final MyCountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    //定期会回调的方法
    public abstract void onTick(long millisUntilFinished);

    //计时结束的回调方法
    public abstract void onFinish();

    private static final int MSG = 1;

    //CountDownTimer采用的是handler机制，通过sendMessageDelayed延迟发送一条message到主线程的looper中，
    //然后在自身中收到之后判断剩余时间，并发出相关回调，然后再次发出message的方式。
    //取消倒计时，把任务从对MessageQueue中移除就好了。
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            synchronized (MyCountDownTimer.this) {
                if (mCancelled) {
                    return;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime(); //剩余时间

                if (millisLeft <= 0) {
                    onFinish();
                } else if (millisLeft < mCountdownInterval) {
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);

                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
                    while (delay < 0) delay += mCountdownInterval;

                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };
}
