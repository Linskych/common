package com.cloudminds.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * The default timezone is UTC
 * */
public class DateTimeUtil {

    private static final Logger log = LoggerFactory.getLogger(DateTimeUtil.class);

    private DateTimeUtil() {}

    public static long currentTimeMillis() {

        return System.currentTimeMillis();
    }

    public static int currentTimeSecond() {
        String sec = String.valueOf(System.currentTimeMillis() / 1000);
        return Integer.parseInt(sec);
    }

    public static Date now() {

        return new Date();
    }

    public static void main(String[] args) {
        System.out.println(now());
    }
}
