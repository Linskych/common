package com.cloudminds.framework.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName = "i18nFilter", urlPatterns = "/*")
@Order(1990)
public class I18nFilter implements Filter {

    @Value("${spring.messages.param}")
    private String param;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String lang = request.getParameter(param);
        System.out.println("i18nFilter: " + lang);
    }
}
