package com.cloudminds.framework.i18n;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


@Component
public class I18nLangUtil {

    @Autowired
    private MessageSource messageSource;

    public String getMsg(String key, Object[] args, String defaultMsg) {

        return messageSource.getMessage(key, args, defaultMsg, LocaleContextHolder.getLocale());
    }

    public String getMsg(String key, Object[] args) {

        return getMsg(key, args, StringUtils.EMPTY);
    }

    public String getMsg(String key) {

        return getMsg(key, null, StringUtils.EMPTY);
    }

}
