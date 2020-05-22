package com.cloudminds.framework.repo.cache.redis;

public class RedisKeyUtil {

    private static final String LOCK_KEY_PREFIX = "REDIS_LOCK_";

    public static String formatLockKey(String key) {

        return LOCK_KEY_PREFIX + key;
    }
}
