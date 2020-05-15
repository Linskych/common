package com.cloudminds.framework;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

@SpringBootTest
class FrameworkApplicationTests {

    @Autowired
    private ApplicationContext ctx;

    @Test
    void contextLoads() {
        Object obj = ctx.getBean(MessageSourceAccessor.class);
        System.out.println(ctx.getMessage("swm.hello", null, Locale.CHINA));
    }

}
