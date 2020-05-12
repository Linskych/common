package com.cloudminds.framework.redis.lock;

import com.cloudminds.framework.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class RedisLock {

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    private String key;//The key to be lock
    private String token;//Set as value of the key
    private RedisService redisService;
    private long expireMills;//The lock last time in millSecond
    private boolean retry;//Retry when fail to lock or not
    private boolean success;//Hold a lock successfully


    //Please do not remove any blank.
    private static final String UNLOCK_SCRIPT = " local val = redis.call( 'get', KEYS[1] )" +
                                                " if ( val == false )" +
                                                " then" +
                                                    " return 2" +
                                                " elseif ( val == ARGV[1] )" +
                                                " then" +
                                                    " return redis.call( 'del', KEYS[1] )"+
                                                " else" +
                                                    " return 0" +
                                                " end";



    private RedisLock() {}

    public RedisLock(String key, String token, RedisService redisService, long expireMills, boolean retry) {
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
                boolean lock = redisService.setNxPx(RedisLockUtil.formatKey(key), token, expireMills);
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
     *         But for current client the lock was released successfully.
     * */
    public Boolean unlock() {
        if (!success) {
            return Boolean.FALSE;
        }
        try {
            //This avoids that a client will try to release the lock after the expire time deleting the key created by another client that acquired the lock later.
            Long exeResult = redisService.execute(UNLOCK_SCRIPT, Long.class, Collections.singletonList(RedisLockUtil.formatKey(key)), token);
            if (null != exeResult && exeResult >= 1) {
                log.debug("The raw result of tryUnlock operation: {}(1-del success, 2-not exist key, 0-fail to del)", exeResult);
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("Try to unlock {} with value {} unsuccessfully.", key, token, e);
        }
        return Boolean.FALSE;
    }

    public String getKey() {
        return key;
    }

    public String getToken() {
        return token;
    }

}
