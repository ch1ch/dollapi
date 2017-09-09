package com.dollapi.exception;

public class DollException extends RuntimeException {
    private Integer code;
    private String error;

    public DollException(Integer code,String error){
        this.code=code;
        this.error=error;
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
}
