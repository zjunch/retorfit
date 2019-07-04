package com.android.retorfit.network;

/**
 * 统一响应
 * @param <T>
 */
public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }
    public void setData(T demo) {
        this.data = demo;
    }
}
