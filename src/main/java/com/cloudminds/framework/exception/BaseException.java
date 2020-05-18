package com.cloudminds.framework.exception;

public class BaseException extends RuntimeException {

    private String i18nKey;
    private int code;

    public BaseException() {
    }

    public BaseException(int code, String i18nKey, String message) {
        super(message);
        this.code = code;
        this.i18nKey = i18nKey;
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public void setI18nKey(String i18nKey) {
        this.i18nKey = i18nKey;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
