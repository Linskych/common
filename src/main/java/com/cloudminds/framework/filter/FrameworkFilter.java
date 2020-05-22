package com.cloudminds.framework.filter;

import com.cloudminds.framework.i18n.I18nProperties;
import com.cloudminds.framework.response.ResponseUtil;
import com.cloudminds.framework.response.R;
import com.cloudminds.framework.serialnum.SerialNumGenerator;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;


/**
 * This filter's purpose is using as most outer filter of application
 * */
@WebFilter(filterName = "frameworkFilter", urlPatterns = "/*")
@Order(2020)
public class FrameworkFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(FrameworkFilter.class);

    @Autowired
    private I18nProperties i18nProperties;
    @Autowired
    @Qualifier("uuidGenerator")
    private SerialNumGenerator serialNumGenerator;
    @Autowired
    private ResponseUtil responseUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            initLogTraceId(request);
        } catch (Exception e) {
            log.warn("Fail to init log trace id.", e);
        }

        try {
            initLangLocale(request);
        } catch (Exception e) {
            log.warn("Fail to init locale lang.", e);
        }

        try {
            //always called
            chain.doFilter(request, response);
        } catch (Exception e) {
            R r = responseUtil.unknownException();
            responseUtil.httpResponse(response, r);
            log.error("Unknown error.", e);
        } finally {
            try {
                ThreadContext.clearMap();
            } catch (Exception e) {
                log.warn("Fail to clear locale lang context.", e);
            }
        }

    }

    /**
     * @TODO How to get trace id for async method running in another thread.
     *       Refer to http://chenbo.me/archives/224
     * */
    private void initLogTraceId(ServletRequest request) {
        String traceId = request.getParameter("traceId");
        if (StringUtils.isEmpty(traceId)) {
            traceId = serialNumGenerator.getSerialNum().substring(0, 16);
        }
        ThreadContext.put("traceId", traceId);
    }

    private void initLangLocale(ServletRequest request) {
        String lang = request.getParameter(i18nProperties.getLangParamName());
        if (StringUtils.isEmpty(lang)) {
            lang = i18nProperties.getDefaultLang();
        }
        Locale locale = StringUtils.parseLocale(lang);

        LocaleContextHolder.setLocale(locale);

        if (i18nProperties.isUseSession()) {
            ((HttpServletRequest) request).getSession().setAttribute(i18nProperties.getLangAttributeName(), locale);
        }
    }
}
