package com.qy.zgz.mall.utils;

import android.os.Handler;

/**
 * 定时操作
 */
public class HandlerUtil {
    private static Handler handler=new Handler();
    private NextListener nextListener;
    private int number=1;
    public HandlerUtil(NextListener nextListener)
    {
        this.nextListener=nextListener;
    }

    public void startNextInSecond(int millSecond)
    {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,millSecond);
    }

    public void start()
    {
        handler.removeCallbacks(runnable);
        number=1;
        nextListener.next(number);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            number++;
            nextListener.next(number);
        }
    };

    public interface NextListener
    {
        public void next(int number);
    }
}
