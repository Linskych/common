package com.cloudminds.framework.i18n;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;


@Configuration
public class I18nConfig implements WebMvcConfigurer {

    @Value("${spring.messages.basename:messages}")
    private String basename;
    @Value("${spring.messages.encoding:UTF-8}")
    private String encoding;
    @Value("${spring.messages.fallback-to-system-locale:false}")
    private boolean fallbackToSystemLocale;
    @Value("${spring.messages.use-code-as-default-message:true}")
    private boolean useCodeAsDefaultMessage;

    @Value("${spring.messages.param}")
    private String param;


//*********************common config******************************
    /**
     * This is for spring, not just web.
     * */
    @Bean
    @ConditionalOnMissingBean(MessageSource.class)
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(basename.split(","));
        messageSource.setDefaultEncoding(encoding);
        messageSource.setFallbackToSystemLocale(fallbackToSystemLocale);//If this is set as true, setDefaultLocale(Locale.ENGLISH) will not work
        messageSource.setDefaultLocale(Locale.ENGLISH);//Used when the target locale not found
        messageSource.setUseCodeAsDefaultMessage(useCodeAsDefaultMessage);

        return messageSource;
    }

//*********************web config******************************

    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    @ConditionalOnWebApplication
    public SessionLocaleResolver localeResolver() {

        SessionLocaleResolver localeResolver = new SessionLocaleResolver();

        localeResolver.setLocaleAttributeName(WebI18nUtil.I18N_LANG_IN_SESSION);
        localeResolver.setTimeZoneAttributeName(WebI18nUtil.I18N_TIMEZONE_IN_SESSION);

        return localeResolver;
    }

    @Bean
    @ConditionalOnBean(SessionLocaleResolver.class) //Change the condition when localeResolver() return type changed.
    public LocaleChangeInterceptor localeChangeInterceptor() {

        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(param);

        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = localeChangeInterceptor();
        if (interceptor != null) {
            registry.addInterceptor(interceptor);
        }
    }
}
