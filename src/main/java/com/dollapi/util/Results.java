package com.dollapi.util;


/**
 * Created by hexu on 2017/9/2.
 */
public class Results {
    private Integer code;
    private String error;
    private Object data;

    public Results(){

    }

    public Results(Integer code, String error) {
        this.code = code;
        this.error = error;
    }

    public Results(Integer code, String error, Object data) {
        this.code = code;
        this.error = error;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
