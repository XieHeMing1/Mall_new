package com.qy.zgz.mall.utils;

/**
 * Created by LCB on 2018/5/4.
 */

public class Utils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick(long time) {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= time) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }
}