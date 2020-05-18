package com.cloudminds.framework.exception;

import com.cloudminds.framework.i18n.I18nLangUtil;
import com.cloudminds.framework.response.R;
import com.cloudminds.framework.response.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ConditionalOnWebApplication
@RestControllerAdvice
public class UniformExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UniformExceptionHandler.class);

    @Autowired
    private I18nLangUtil i18nLangUtil;
    @Autowired
    private ResponseUtil responseUtil;

    @ExceptionHandler(Exception.class)
    public R handleUncatchException(Exception e) {
        log.error("Unknown error.", e);
        return responseUtil.operateFailed();
    }

    @ExceptionHandler(ParameterException.class)
    public R handleParamException(ParameterException e) {

        R r = R.err().setMsg(e.getMessage()).setCode(e.getCode());
        return r;
    }
}
