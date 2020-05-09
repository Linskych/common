package com.cloudminds.framework.redis.lock;

import com.cloudminds.framework.redis.RedisService;
import com.cloudminds.framework.serialnum.SerialNumGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

//TODO 1)lock multiple keys;
@Component
public class RedisLock {

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    //Please do not remove any blank.
    private static final String UNLOCK_SCRIPT = "local val = redis.call( 'get', KEYS[1] )" +
                                             " if ( val == false )" +
                                             " then" +
                                                " return 2" +
                                             " elseif ( val == ARGV[1] )" +
                                             " then" +
                                                " return redis.call( 'del', KEYS[1] )"+
                                             " else" +
                                                " return 0" +
                                             " end";

    private static final String REENTRANT_LOCK_SCRIPT = " local val = redis.call( 'set' , KEYS[1] , ARGV[1], 'nx' , 'px' , ARGV[2] )" +
                                                        " if ( val == false )" +
                                                        " then" +
                                                            " if ( ARGV[1] == redis.call( 'get', KEYS[1] ) )" +
                                                            " then" +
                                                                " return 2" +
                                                            " else" +
                                                                " return 0" +
                                                            " end" +
                                                        " else" +
                                                            " return 1" +
                                                        " end";

    private static final String LOCK_KEY_PREFIX = "REDIS_LOCK";
    private static final int INTERVAL_TIME = 50;//ms

    @Autowired
    private RedisService redisService;
    @Autowired
    @Qualifier("uuidGenerator")
    private SerialNumGenerator serialNumGenerator;


    /**
     * @param token The value set by lockKey. If it is empty, a uuid will be assign to it.
     * @param mills Expire time(millisecond)
     * @return Empty String if do not get lock, or the token which was set as value of lockKey. This will be used when tryUnlock lockKey.
     * */
    public String tryLock(String lockKey, String token, long mills, boolean retry) {
        if (StringUtils.isEmpty(lockKey)) {
            log.warn("LockKey MUST NOT be empty.");
            return StringUtils.EMPTY;
        }
        if (mills <= 0) {
            log.warn("Expire time MUST be more than zero.");
            return StringUtils.EMPTY;
        }
        if (StringUtils.isEmpty(token)) {
            token = serialNumGenerator.getSerialNum();
        }
        long timeout = mills; //Use mills as timeout.
        long start = System.currentTimeMillis();
        long end = start + timeout;
        while (true) {
            try {
                boolean lock = redisService.setNxPx(LOCK_KEY_PREFIX  + lockKey, token, mills);
                if (lock) {
                    return token;
                }
            } catch (Exception e) {
                log.error("Try to get redis lock error.\n", e);
            }
            if (!retry) {
                return StringUtils.EMPTY;
            }
            if (timeout < INTERVAL_TIME || System.currentTimeMillis() > end) {
                log.info("Try to get redis lock {} timeout.", lockKey);
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(INTERVAL_TIME);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted.\n", e);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @param mills expire time(millisecond)
     * @return An empty if do not get lock, or an uuid which was set as value of lockKey and this uuid will be used when tryUnlock lockKey.
     * */
    public String tryLock(String lockKey, long mills) {

        return tryLock(lockKey, StringUtils.EMPTY, mills, Boolean.FALSE);
    }

    /**
     * @param token The value set by lockKey. If it is empty, a uuid will be assign to it.
     * @param mills Expire time(millisecond)
     * @return Empty String if do not get lock, or the token which was set as value of lockKey. This will be used when tryUnlock lockKey.
     * */
    public String tryReentrantLock(String lockKey, String token, long mills, boolean retry, boolean lastExpireTime) {
        if (StringUtils.isEmpty(lockKey)) {
            log.warn("LockKey MUST NOT be empty.");
            return StringUtils.EMPTY;
        }
        if (mills <= 0) {
            log.warn("Expire time MUST be more than zero.");
            return StringUtils.EMPTY;
        }
        //Only the first time try to get lock can retry.
        retry = retry && StringUtils.isEmpty(token);
        if (StringUtils.isEmpty(token)) {
            token = serialNumGenerator.getSerialNum();
        }
        long timeout = mills; //Use mills as timeout.
        long start = System.currentTimeMillis();
        long end = start + timeout;
        while (true) {
            try {
                Long exeResult = redisService.execute(REENTRANT_LOCK_SCRIPT, Long.class, Collections.singletonList(LOCK_KEY_PREFIX + lockKey), token, mills);
                if (exeResult > 0) {
                    log.debug("The raw result of tryReentrantLock operation: {}(0-can not get lock, 1-first get lock, 2-get reentrant lock)", exeResult);
                    return token;
                }
            } catch (Exception e) {
                log.error("Try to get redis lock error.\n", e);
            }
            if (!retry) {
                return StringUtils.EMPTY;
            }
            if (timeout < INTERVAL_TIME || System.currentTimeMillis() > end) {
                log.info("Try to get redis lock {} timeout.", lockKey);
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(INTERVAL_TIME);
            } catch (InterruptedException e) {
                log.error("Sleep interrupted.\n", e);
            }
        }
        return StringUtils.EMPTY;
    }

    public boolean tryUnlock(String lockKey, String uniqueLockValue) {

        if (StringUtils.isEmpty(lockKey) || StringUtils.isEmpty(uniqueLockValue)) {
            log.warn("LockKey and uniqueLockValue are both necessary.");
            return false;
        }
        try {
            Long exeResult = redisService.execute(UNLOCK_SCRIPT, Long.class, Collections.singletonList(LOCK_KEY_PREFIX + lockKey), uniqueLockValue);
            if (null != exeResult && exeResult >= 1) {
                log.debug("The raw result of tryUnlock operation: {}(1-del success, 2-not exist key, 0-fail to del or val not match)", exeResult);
                return true;
            }
        } catch (Exception e) {
            log.error("Try to unlock {} with value {} unsuccessfully.", lockKey, uniqueLockValue, e);
            return false;
        }
        return false;
    }
}
