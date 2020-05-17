package com.cloudminds.framework.i18n;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class LangLocaleResolver implements LocaleResolver {

    private String langParamName;
    private String defaultLang;
    private boolean useSession;
    private String langAttributeName;

    public LangLocaleResolver() {
    }

    public LangLocaleResolver(String langParamName, String defaultLang, boolean useSession, String langAttributeName) {
        this.langParamName = langParamName;
        this.defaultLang = defaultLang;
        this.useSession = useSession;
        this.langAttributeName = langAttributeName;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        Locale locale = null;
        if (useSession) {
            locale = (Locale) request.getSession().getAttribute(langAttributeName);
            if (locale != null) {
                return locale;
            }
        }

        String lang = request.getParameter(langParamName);
        if (StringUtils.isEmpty(lang)) {
            lang = defaultLang;
        }
        return  StringUtils.parseLocale(lang);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {

    }
}
