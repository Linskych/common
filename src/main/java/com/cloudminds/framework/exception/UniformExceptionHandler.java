package com.cloudminds.framework.exception;

import com.cloudminds.framework.response.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ConditionalOnWebApplication
@RestControllerAdvice
public class UniformExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UniformExceptionHandler.class);

    @ExceptionHandler(ParameterException.class)
    public R handleParamException(ParameterException e) {

        R r = R.err().setMsg(e.getMessage()).setCode(e.getCode());
        return r;
    }
}
