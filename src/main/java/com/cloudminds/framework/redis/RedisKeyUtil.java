package com.cloudminds.framework.redis;

public class RedisKeyUtil {

    private static final String LOCK_KEY_PREFIX = "REDIS_LOCK_";

    public static String formatLockKey(String key) {

        return LOCK_KEY_PREFIX + key;
    }
}
