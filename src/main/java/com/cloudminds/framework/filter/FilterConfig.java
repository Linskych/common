package com.cloudminds.framework.filter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

/**
 * Empty class just for enable:
 * 1. WebServlet
 * 2. WebFilter
 * 3. WebListener
 *
 * @note change the package if necessary
 * */

@Component
@ServletComponentScan("com.cloudminds")
public class FilterConfig {
}
