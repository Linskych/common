package com.cloudminds.framework.exception;

public class ParameterAssert implements Assert{


    @Override
    public BaseException exception() {

        return new ParameterException();
    }
}
