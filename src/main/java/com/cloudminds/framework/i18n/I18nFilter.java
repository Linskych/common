package com.cloudminds.framework.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

@WebFilter(filterName = "i18nFilter", urlPatterns = "/*")
@Order(2020)
public class I18nFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(I18nFilter.class);

    @Autowired
    private I18nProperties i18nProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String lang = request.getParameter(i18nProperties.getLangParamName());
        if (StringUtils.isEmpty(lang)) {
            lang = i18nProperties.getDefaultLang();
        }
        Locale locale = StringUtils.parseLocale(lang);

        LocaleContextHolder.setLocale(locale);

        if (i18nProperties.isUseSession()) {
            ((HttpServletRequest) request).getSession().setAttribute(i18nProperties.getLangAttributeName(), locale);
        }

        chain.doFilter(request, response);
    }
}
