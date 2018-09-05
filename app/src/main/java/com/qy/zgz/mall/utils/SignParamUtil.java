package com.qy.zgz.mall.utils;

import android.util.Log;

import com.qy.zgz.mall.network.Constance;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * 加密参数 类
 * Created by ZYB on 2017/11/21 0021.
 */

public class SignParamUtil {

    public static String getSignStr(HashMap<String,String> param)
    {
        String sign="";
        if (!param.isEmpty()){
            Map<String,String> result=sortMapByKey(param);
            for (Map.Entry<String,String> entry:result.entrySet())
            {
                if (!SomeUtil.TextIsEmpey(entry.getValue()))
                {
                    sign+=entry.getKey()+"="+entry.getValue()+"&";
                }
            }
            sign=sign.substring(0,sign.length()-1);
        }
        sign+= Constance.member_Host_Key;
        Log.i("sign",sign);
        return MD5Utils.getPwd(sign).toLowerCase();
    }
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }


    public static String mapToParam(Map<String, String> map) {
        String param="";
        for (Map.Entry<String,String> entry:map.entrySet())
        {
            if (!SomeUtil.TextIsEmpey(entry.getValue()))
            {
                param+=entry.getKey()+"="+entry.getValue()+"&";
            }
        }
        param=param.substring(0,param.length()-1);

        return param;
    }
}

class MapKeyComparator implements Comparator<String> {

    @Override
    public int compare(String str1, String str2) {

        return str1.compareTo(str2);
    }
}
