package com.qy.zgz.mall.utils;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.qy.zgz.mall.network.XutilsCallback;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LCB on 2018/2/8.
 */

public class HttpUtils {

    //管理请求CALLBACK
    private  static ArrayList<XCancelable> callbackList=new ArrayList<XCancelable>();

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr
     * @return
     * @throws Exception
     */
    public static String doGet(String urlStr)
    {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try
        {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30);
            conn.setConnectTimeout(30);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            if (conn.getResponseCode() == 200)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1)
                {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else
            {
                throw new RuntimeException(" responseCode is not 200 ... ");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (is != null)
                    is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (baos != null)
                    baos.close();
            } catch (IOException e)
            {
            }
            conn.disconnect();
        }

        return null ;

    }


    //xutils POST请求
    public static  <T> void xPostJson( String url, HashMap<String,String> hashMap, XutilsCallback<T> callback) {
        RequestParams params = new RequestParams(url);
        params.addHeader("Content-Type","application/x-www-form-urlencoded");
            for (Map.Entry<String,String> entry:hashMap.entrySet())
            {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        params.setCharset("utf-8");
        params.setConnectTimeout(10000);
        Callback.Cancelable cancelable=x.http().post(params, callback);
        Logger.i(hashMap.toString());
        Logger.i(url);
//        addxHttpToList(context,cancelable);
    }

    public static  <T> void xPostJson( String url, HashMap<String,String> hashMap, XutilsCallback<T> callback, int connectTimeOut) {
        RequestParams params = new RequestParams(url);
        params.addHeader("Content-Type","application/x-www-form-urlencoded");
        for (Map.Entry<String,String> entry:hashMap.entrySet())
        {
            params.addBodyParameter(entry.getKey(), entry.getValue());
        }
        params.setCharset("utf-8");
        params.setConnectTimeout(connectTimeOut);
        Callback.Cancelable cancelable=x.http().post(params, callback);
        Logger.i(hashMap.toString());
        Logger.i(url);
//        addxHttpToList(context,cancelable);
    }

    //取消网络请求
    public static void cancelxHttp(Context context){
        for (XCancelable xCancelable:callbackList){
            if (context==xCancelable.context){
                    try{
                        xCancelable.cancelable.cancel();
                    }catch (Exception e){

                    }
            }
        }

    }

    //添加网络请求到列表中
    public static void addxHttpToList(Context context,Callback.Cancelable cancelable){
        XCancelable xCancelable=new XCancelable(context,cancelable);
        callbackList.add(xCancelable);
    }


    //Cancelable类
    static class XCancelable{

        private Context context;

        private Callback.Cancelable cancelable;

        public XCancelable(Context c,Callback.Cancelable cancel){
            context=c;
            cancelable=cancel;
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public Callback.Cancelable getCancelable() {
            return cancelable;
        }

        public void setCancelable(Callback.Cancelable cancelable) {
            this.cancelable = cancelable;
        }

     }
}
