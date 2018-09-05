package com.qy.zgz.mall.network;

/**
 * 异常数据实体类
 */
public class ApiException extends RuntimeException {
    public int resultCode;
    public ApiException(int resultCode,String message)
    {
        super(message);
        this.resultCode=resultCode;
    }
}
