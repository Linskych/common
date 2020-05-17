package com.cloudminds.framework.filter;

import com.cloudminds.framework.i18n.WebI18nUtil;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

@WebFilter(filterName = "testFilter",urlPatterns = "/*")
public class TestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Locale locale = LocaleContextHolder.getLocale();
        Locale localeSession = (Locale) ((HttpServletRequest) request).getSession().getAttribute(WebI18nUtil.I18N_LANG_IN_SESSION);
        chain.doFilter(request, response);
        Locale after = (Locale) ((HttpServletRequest) request).getSession().getAttribute(WebI18nUtil.I18N_LANG_IN_SESSION);
    }
}
