package com.qy.zgz.mall.utils;

/**
 * 时间管理
 */
public class TimeUtil {
    private static TimeUtil timeUtil;
    private final long dayTime=24*60*60*1000*7l;
    public static TimeUtil getInstance()
    {
        if(timeUtil ==null)
        {
            synchronized (FileManager.class)
            {
                if(timeUtil ==null)
                {
                    timeUtil =new TimeUtil();
                }
            }
        }
        return timeUtil;
    }

    /**
     * 是否超过7天
     */
    public boolean isExceedTime()
    {
        long time=System.currentTimeMillis();
        long saveTime= (long) SharePerferenceUtil.getInstance().getValue("saveTime",0l);
        if((time-saveTime)>dayTime)
        {
            System.out.println("超过");
            SharePerferenceUtil.getInstance().setValue("saveTime",time);
            return true;
        }
        else
        {
            System.out.println("没有超过");
           return false;
        }
    }
}
