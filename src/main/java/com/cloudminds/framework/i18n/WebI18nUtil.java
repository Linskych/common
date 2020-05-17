package com.cloudminds.framework.i18n;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;


@Component
public class WebI18nUtil {

    public static final String I18N_LANG_IN_SESSION = "i18n-lang";
    public static final String I18N_TIMEZONE_IN_SESSION = "i18n-timezone";

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
