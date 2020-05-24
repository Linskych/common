package com.cloudminds.framework.utils.encryption;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesensitizeUtil {

    private static final Logger log = LoggerFactory.getLogger(DesensitizeUtil.class);

    private static final String STARS = "***";

    private DesensitizeUtil() {}

    /**
     * Split account into 3 parts and replace the part in the middle with stars
     * */
    public static String simpleDesensitize(String account) {
        if (StringUtils.isEmpty(account)) {
            return account;
        }
        int size = account.length();
        switch (size){
            case 1:
                return STARS;
            case 2:
                return STARS + account.substring(1);
            default:
                return account.substring(0, size/3) + STARS + account.substring(size*2/3-1, size-1);
        }
    }
}
