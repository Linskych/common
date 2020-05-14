package com.cloudminds.framework.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RedisLock {

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    private String key;//The key to be lock
    private Object token;//Set as value of the key
    private ObjectRedisService redisService;
    private long expireMills;//The lock expire time in milliSecond. Your business run rime should be take into account.
    private boolean retry;//Retry when fail to lock or not
    private boolean success = Boolean.FALSE;;//Hold a lock successfully


    //Please do not remove any blank.
    private static final String UNLOCK_SCRIPT = " local val = redis.call( 'get', KEYS[1] )" +
                                                " if ( val == false )" +
                                                " then" +
                                                    " return 2" +
                                                " elseif ( val == ARGV[1] )" +
                                                " then" +
                                                    " redis.call( 'del', KEYS[1] )" +
                                                    " return 1" +
                                                " else" +
                                                    " return -1" +
                                                " end";

    private RedisLock() {}

    public RedisLock(String key, Object token, ObjectRedisService redisService, long expireMills, boolean retry) {
        this.key = key;
        this.token = token;
        this.redisService = redisService;
        this.expireMills = expireMills;
        this.retry = retry;
    }

    public Boolean tryLock() {

        long timeout = expireMills; //Use expireMills as timeout.
        long start = System.currentTimeMillis();
        long end = start + timeout;

        while (true) {
            try {
                boolean lock = redisService.setNxPx(RedisKeyUtil.formatLockKey(key), token, expireMills);
                if (lock) {
                    return success = Boolean.TRUE;
                }
            } catch (Exception e) {
                log.error("Try to get redis lock error.\n", e);
            }

            if (!retry) {
                break;
            }

            if (timeout < RedisLockUtil.INTERVAL_TIME || System.currentTimeMillis() > end) {
                log.info("Try to get redis lock {} timeout.", key);
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(RedisLockUtil.INTERVAL_TIME);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted.\n", e);
            }
        }

        return Boolean.FALSE;
    }

    /**
     * @return If the key was deleted after expire time and another client holds the lock, it will return false too.
     *         But for current client the lock was released successfully. Maybe you should not use this result as a condition of your business logic.
     * */
    public Boolean unlock() {
        if (!success) {
            return Boolean.FALSE;
        }
        try {
            //This avoids that a client will try to release the lock after the expire time deleting the key created by another client that acquired the lock later.
            Long exeResult = redisService.execute(UNLOCK_SCRIPT, Long.class, Collections.singletonList(RedisKeyUtil.formatLockKey(key)), token);
            log.debug("The raw result of tryUnlock operation: {}(1-del success, 2-not exist key, 0-fail to del)", exeResult);
            if (exeResult != null && exeResult > 0) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("Try to unlock {} with value {} unsuccessfully.", key, JacksonSerializerUtil.toJson(token), e);
        }
        return Boolean.FALSE;
    }

    public String getKey() {
        return key;
    }

    public Object getToken() {
        return token;
    }

}
