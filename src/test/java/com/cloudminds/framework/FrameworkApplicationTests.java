package com.cloudminds.framework;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

@SpringBootTest
class FrameworkApplicationTests {

    @Autowired
    private WebApplicationContext ctx;

    @Test
    void contextLoads() {
        System.out.println(ArrayUtils.toString(ctx.getBeanDefinitionNames()));
        System.out.println(ctx.getMessage("swm.hello", null, Locale.CHINA));
    }

}
