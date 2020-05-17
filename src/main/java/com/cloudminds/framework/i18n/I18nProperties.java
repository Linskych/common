package com.cloudminds.framework.i18n;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class I18nProperties {

    @Value("${spring.messages.basename:messages}")
    private String basename;//i18n base name join by ",", such as "i18n/common,i18n/app"
    @Value("${spring.messages.encoding:UTF-8}")
    private String encoding;
    @Value("${spring.messages.fallback-to-system-locale:false}")
    private boolean fallbackToSystemLocale;//use system locale if true when parse locale unsuccessfully, or use default lang if false and default lang set.
    @Value("${spring.messages.use-code-as-default-message:true}")
    private boolean useCodeAsDefaultMessage;//use key as result if true when no related value found, or empty string if false
    @Value("${spring.messages.defaultLang:en}")
    private String defaultLang;//use this lang when parse locale unsuccessfully and fallbackToSystemLocale set as false
    @Value("${spring.messages.use-session:false}")
    private boolean useSession;//set lang into http session if true
    @Value("${spring.messages.lang-attribute-name:i18n-lang}")
    private String langAttributeName;//the attribute name when set lang into http session
    @Value("${spring.messages.lang-param-name:lang}")
    private String langParamName;//request.getParameter(langParamName)

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
