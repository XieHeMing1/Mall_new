package com.qy.zgz.mall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.qy.zgz.mall.MyApplication;

/**
 * 缓存管理
 */
public class SharePerferenceUtil {
    private SharedPreferences sharedPreferences;
    private static SharePerferenceUtil instance;
    private final String FILE_NAME="CRANEMA";
    public static SharePerferenceUtil getInstance()
    {
        if(instance==null)
        {
            synchronized (SharePerferenceUtil.class)
            {
                if(instance==null)
                {
                    instance=new SharePerferenceUtil();
                }
            }
        }
        return instance;
    }

    private  SharePerferenceUtil()
    {
//        sharedPreferences= MyApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
    }

    public void setValue(String key,Object object)
    {
        String type=object.getClass().getSimpleName();
        SharedPreferences.Editor editor=sharedPreferences.edit();
        switch (type)
        {
            case "String":
                editor.putString(key,object.toString());
                break;
            case "Integer":
                editor.putInt(key,(Integer)object);
                break;
            case "Boolean":
                editor.putBoolean(key,(Boolean)object);
                break;
            case "Float":
                editor.putFloat(key,(Float)object);
                break;
            case "Long":
                editor.putLong(key,(Long)object);
                break;
        }
        editor.commit();
    }

    public Object getValue(String key,Object defaultObject)
    {
        String type=defaultObject.getClass().getSimpleName();
        switch (type)
        {
            case "String":
                return sharedPreferences.getString(key,(String)defaultObject);
            case "Integer":
                return sharedPreferences.getInt(key,(Integer) defaultObject);
            case "Boolean":
                return sharedPreferences.getBoolean(key,(Boolean)defaultObject);
            case "Float":
                return sharedPreferences.getFloat(key,(Float)defaultObject);
            case "Long":
                return sharedPreferences.getLong(key,(Long)defaultObject);
        }
        return null;
    }
}
