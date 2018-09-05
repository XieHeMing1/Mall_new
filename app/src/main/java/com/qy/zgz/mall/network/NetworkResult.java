package com.qy.zgz.mall.network;

/**
 * 服务器返回的数据
 * @param <T>
 */
public class NetworkResult<T> {
    private String msg;
    private int errorcode;
    private T data;
    private boolean flag;

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public int getCode() {
        return errorcode;
    }

    public void setCode(int code) {
        this.errorcode = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
