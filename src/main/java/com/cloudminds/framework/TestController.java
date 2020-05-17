package com.cloudminds.framework;

import com.cloudminds.framework.i18n.WebI18nUtil;
import com.cloudminds.framework.response.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private WebI18nUtil i18nUtil;

    @GetMapping("/lang")
    public R testLang(HttpServletRequest request) {
        Locale locale = request.getLocale();
        Locale localeSession = (Locale) request.getSession().getAttribute(WebI18nUtil.I18N_LANG_IN_SESSION);
        Locale localeHolder = LocaleContextHolder.getLocale();
        String msg = i18nUtil.getMsg("swm.hello");
        String mm = messageSource.getMessage("swm.hello", null, "", localeSession);
        R r = R.ok().setMsg(msg);
        return r;
    }

    @Autowired
    private ResourceBundleMessageSource messageSource;

    public String getMsg(String key, Object[] args, String defaultMsg) {

        return messageSource.getMessage(key, args, defaultMsg, LocaleContextHolder.getLocale());
    }
}
