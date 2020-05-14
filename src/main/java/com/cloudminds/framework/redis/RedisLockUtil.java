package com.cloudminds.framework.redis;

import com.cloudminds.framework.serialnum.SerialNumGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


//TODO 1)lock multiple keys;
@Component
public class RedisLockUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisLockUtil.class);

    private static final String LOCK_KEY_PREFIX = "REDIS_LOCK_";
    public static final int INTERVAL_TIME = 50;//ms, 50~100
    private static final boolean RETRY = Boolean.TRUE;
    private static final long NEW_EXPIRE_MILLS_ZERO = 0;

    @Autowired
    private ObjectRedisService redisService;
    @Autowired
    @Qualifier("uuidGenerator")
    private SerialNumGenerator serialNumGenerator;


    public RedisLock createLock(String lockKey, long mills) {

        return createLock(lockKey, StringUtils.EMPTY, mills, RETRY);
    }

    public RedisLock createLock(String lockKey, String token, long mills) {

        return createLock(lockKey, token, mills, RETRY);
    }

    public RedisLock createLock(String lockKey, long mills, boolean retry) {

        return createLock(lockKey, StringUtils.EMPTY, mills, retry);
    }

    public RedisLock createLock(String lockKey, String token, long mills, boolean retry) {
        if (StringUtils.isEmpty(lockKey)) {
            throw new RuntimeException("LockKey MUST NOT be empty.");
        }
        if (mills <= 0) {
            throw new RuntimeException("Expire time MUST be more than zero.");
        }
        if (StringUtils.isEmpty(token)) {
            token = serialNumGenerator.getSerialNum();
        }

        return new RedisLock(lockKey, token, redisService, mills, retry);
    }

    public ReentrantLock createReentrantLock(String lockKey, long expireMillis) {

        return createReentrantLock(lockKey, StringUtils.EMPTY, expireMillis, RETRY, NEW_EXPIRE_MILLS_ZERO);
    }

    public ReentrantLock createReentrantLock(String lockKey, String token, long expireMillis) {

        return createReentrantLock(lockKey, token, expireMillis, RETRY, NEW_EXPIRE_MILLS_ZERO);
    }

    public ReentrantLock createReentrantLock(String lockKey, long expireMillis, boolean retry) {

        return createReentrantLock(lockKey, StringUtils.EMPTY, expireMillis, retry, NEW_EXPIRE_MILLS_ZERO);
    }

    public ReentrantLock createReentrantLock(String lockKey, String token, long expireMillis, boolean retry) {

        return createReentrantLock(lockKey, token, expireMillis, retry, NEW_EXPIRE_MILLS_ZERO);
    }

    public ReentrantLock createReentrantLock(String lockKey, String token, long expireMillis, boolean retry, long newExpireMillis) {
        if (StringUtils.isEmpty(lockKey)) {
            throw new RuntimeException("LockKey MUST NOT be empty.");
        }
        if (expireMillis <= 0) {
            throw new RuntimeException("Expire time MUST be more than zero.");
        }
        if (StringUtils.isEmpty(token)) {
            token = serialNumGenerator.getSerialNum();
        }

        return new ReentrantLock(lockKey, token, redisService, expireMillis, retry, newExpireMillis);
    }

    public static String formatKey(String key) {

        return LOCK_KEY_PREFIX + key;
    }

}
