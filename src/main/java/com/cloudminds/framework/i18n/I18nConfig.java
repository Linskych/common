package com.cloudminds.framework.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;


@Configuration
public class I18nConfig {

    @Autowired
    private I18nProperties i18nProperties;


    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(i18nProperties.getBasename().split(","));
        messageSource.setDefaultEncoding(i18nProperties.getEncoding());
        messageSource.setFallbackToSystemLocale(i18nProperties.isFallbackToSystemLocale());//If this is set as true, setDefaultLocale(Locale.ENGLISH) will not work
        messageSource.setDefaultLocale(StringUtils.parseLocale(i18nProperties.getDefaultLang()));//Used when the target locale not found
        messageSource.setUseCodeAsDefaultMessage(i18nProperties.isUseCodeAsDefaultMessage());

        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LangLocaleResolver localeResolver() {

        return new LangLocaleResolver(
                i18nProperties.getLangParamName(),i18nProperties.getDefaultLang(),
                i18nProperties.isUseSession(), i18nProperties.getLangAttributeName());
    }
}
