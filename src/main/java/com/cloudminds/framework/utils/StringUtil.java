package com.cloudminds.framework.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class StringUtil {

    private static final Logger log = LoggerFactory.getLogger(StringUtil.class);

    public static final String COMMA = ",";

    private StringUtil() {}

    public static List<String> splitAsList(String commaString) {

        if (StringUtils.isEmpty(commaString)) {
            return Lists.newArrayList();
        }
        return Arrays.asList(commaString.split(COMMA));
    }

    public static List<Integer> splitAsIntList(String commaString) {
        List<Integer> list = Lists.newArrayList();

        if (StringUtils.isEmpty(commaString)) {
            return list;
        }

        String[] elements = commaString.split(COMMA);
        for (String element : elements) {
            list.add(Integer.valueOf(element));
        }
        return list;
    }

    public static List<Long> splitAsLongList(String commaString) {
        List<Long> list = Lists.newArrayList();

        if (StringUtils.isEmpty(commaString)) {
            return list;
        }

        String[] elements = commaString.split(COMMA);
        for (String element : elements) {
            list.add(Long.valueOf(element));
        }
        return list;
    }
}
