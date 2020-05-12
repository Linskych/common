package com.cloudminds.framework.exception;

public class BaseException extends RuntimeException {

    private int code;

    public BaseException() {
    }

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
