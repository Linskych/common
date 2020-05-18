package com.cloudminds.framework.exception;

public class ParameterException extends BaseException {


    public ParameterException() {
    }

    public ParameterException(int code, String i18nKey, String message) {
        super(code, i18nKey, message);
    }
}
