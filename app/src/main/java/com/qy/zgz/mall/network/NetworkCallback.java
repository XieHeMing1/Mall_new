package com.qy.zgz.mall.network;


import rx.Subscriber;

/**
 * 网络返回的回调接口，可以在此对异常数据进行处理
 * @param <T>
 */
public abstract class NetworkCallback<T> extends Subscriber<T> {
    public abstract void onSuccess(T data);
    public abstract void onFailure(int code,String msg);
    @Override
    public void onCompleted(){

    }



    /**
     * 对错误进行统一处理
     * onCompleted和onError相互排斥,只会触发其中一个。
     */
    @Override
    public void onError(Throwable e)
    {
        if(e instanceof ApiException)
        {
            ApiException apiException=(ApiException) e;
            onFailure(apiException.resultCode,apiException.getMessage());
        }
        else
        {
            onNetWorkFailure((Exception) e);
//            onFailure(0,"请检查网络是否正常");
        }
        //主要用来隐藏加载弹窗
        onCompleted();
        e.printStackTrace();
    }

    @Override
    public void onNext(T t)
    {
        onSuccess(t);
    }

    public  void onNetWorkFailure(Exception e){

    }
}

