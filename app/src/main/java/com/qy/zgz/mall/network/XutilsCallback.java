package com.qy.zgz.mall.network;


import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;

/**
 * 网络返回的回调接口，可以在此对异常数据进行处理
 * @param <T>
 */
public abstract class XutilsCallback<T> implements Callback.CommonCallback<T> {
    public abstract void onSuccessData(T result);

    @Override
    public void onSuccess(T result) {
        try {
            Logger.i(result.toString());
            onSuccessData(result);
        }catch (Exception e){

        }

    }


}

