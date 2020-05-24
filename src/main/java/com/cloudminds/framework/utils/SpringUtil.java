package com.cloudminds.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {

        return SpringUtil.ctx.getBean(type);
    }

    public static <T> T getBean(String beanName, Class<T> type) {

        return SpringUtil.ctx.getBean(beanName, type);
    }

    public static <T> T getBean(String beanName) {

        return (T) SpringUtil.ctx.getBean(beanName);
    }
}
