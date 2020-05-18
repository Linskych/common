package com.cloudminds.framework.i18n;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class I18nProperties {

    //i18n base name join by ",", such as "i18n/common,i18n/app"
    @Value("${spring.messages.basename:messages}")
    private String basename;

    @Value("${spring.messages.encoding:UTF-8}")
    private String encoding;

    //use system locale if true when parse locale unsuccessfully, or use default lang if false and default lang set.
    @Value("${spring.messages.fallback-to-system-locale:false}")
    private boolean fallbackToSystemLocale;

    //use key as result if true when no related value found, or empty string if false
    @Value("${spring.messages.use-code-as-default-message:true}")
    private boolean useCodeAsDefaultMessage;

    //use this lang when parse locale unsuccessfully and fallbackToSystemLocale set as false
    @Value("${spring.messages.lang-default:en}")
    private String defaultLang;

    //set lang into http session if true
    @Value("${spring.messages.use-session:false}")
    private boolean useSession;

    //the attribute name when set lang into http session
    @Value("${spring.messages.lang-attribute-name:i18n-lang}")
    private String langAttributeName;

    //request.getParameter(langParamName)
    @Value("${spring.messages.lang-param-name:lang}")
    private String langParamName;


    public String getBasename() {
        return basename;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isFallbackToSystemLocale() {
        return fallbackToSystemLocale;
    }

    public boolean isUseCodeAsDefaultMessage() {
        return useCodeAsDefaultMessage;
    }

    public String getDefaultLang() {
        return defaultLang;
    }

    public boolean isUseSession() {
        return useSession;
    }

    public String getLangParamName() {
        return langParamName;
    }

    public String getLangAttributeName() {
        return langAttributeName;
    }
}
