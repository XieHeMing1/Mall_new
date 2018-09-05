
package com.qy.zgz.mall.utils;

import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/21 0021.
 */
public class SomeUtil {
    private static Intent intent;


    //判断字符串是否为空
    public static boolean TextIsEmpey(String str) {
        if (null == str || 0 == str.length()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 将当前时间转换为特定格式的字符串
     *
     * @return
     */
    public static String formatDate() {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(new Date());
        return date;
    }

    /**
     * 将时间戳转换为特定格式的字符串
     */
    public static String longToDate(long time) {
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(time);
        return date;
    }

    /**
     * 切割字符串
     *
     * @param str       原字符串
     * @param split     切割的字符
     * @param isgetlast 是否直接获得切割后最后一项
     * @param index     获取切割后数组对应的下标
     * @return
     */
    public static String splitStr(String str, String split, boolean isgetlast, int index) {
        String[] strs = str.split(split);
        if (isgetlast)
            return strs[strs.length - 1];
        else {
            if (index >= 0 && index <= strs.length - 1)
                return strs[index];
        }
        return "";
    }
}
