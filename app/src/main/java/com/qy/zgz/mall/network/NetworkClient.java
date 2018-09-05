package com.qy.zgz.mall.network;

import android.util.Log;

import com.qy.zgz.mall.MyApplication;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络组建初始化
 */
public class NetworkClient {
    private static NetworkClient instance=null;
    private static String tokenTemp="";
    final XBApiService mService;
    private Retrofit retrofit;
    public OkHttpClient okHttpClient;
    public static NetworkClient getInstance()
    {
        if(instance==null)
        {
            instance=new NetworkClient();
        }
        return instance;
    }

    private NetworkClient()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.i("RetrofitLog","retrofitBack = "+message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor interceptor=chain->chain.proceed(chain.request().newBuilder()
                    .header("x-fr-token",tokenTemp)
                    .addHeader("x-login-type","normal")
                    .addHeader("x-terminal-type","android")
                    .build());
        //设置Http缓存
        Cache cache=new Cache(new File(MyApplication.getInstance().getCacheDir(),
                "HttpCache"),1024*1024*10);
        okHttpClient=new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)//打印数据信息
                .retryOnConnectionFailure(true)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        retrofit=new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(Constance.HOST)
                .build();
        mService=retrofit.create(XBApiService.class);
    }
}
