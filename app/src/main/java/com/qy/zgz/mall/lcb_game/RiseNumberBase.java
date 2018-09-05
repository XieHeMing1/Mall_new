package com.qy.zgz.mall.lcb_game;

/**
 * Created by LCB on 2018/8/16.
 */

/**
 * 数字动画自定义
 *
 *
 */
public interface RiseNumberBase {
    public void start();
    public RiseNumberTextView withNumber(float number);
    public RiseNumberTextView withNumber(float number, boolean flag);
    public RiseNumberTextView withNumber(int number);
    public RiseNumberTextView setDuration(long duration);
    public void setOnEnd(RiseNumberTextView.EndListener callback);
    public void setOnTimeTick(RiseNumberTextView.TimeTickListener callback);
}