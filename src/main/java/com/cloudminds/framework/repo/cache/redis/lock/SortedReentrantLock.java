package com.cloudminds.framework.repo.cache.redis.lock;

import com.cloudminds.framework.repo.cache.redis.ObjectRedisService;
import com.cloudminds.framework.repo.cache.redis.RedisKeyUtil;
import com.cloudminds.framework.json.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SortedReentrantLock {

    private static final Logger log = LoggerFactory.getLogger(SortedReentrantLock.class);

    private String key;//The key to be lock
    private Object token;//Set as value of the key
    private ObjectRedisService redisService;
    private long expireMillis;//The lock last time in millSecond
    private boolean retry;//Retry when fail to lock or not
    private long newExpireMillis;//This will be set as new expire time when re-entrant the lock.
    private Boolean success = Boolean.FALSE;;


    //Please do not remove any blank.
    private static final String UNLOCK_SCRIPT = " local val = redis.call( 'get', KEYS[1] )" +
                                                " if ( val == false )" +
                                                " then" +
                                                    " return 3" +
                                                " elseif ( val == ARGV[1] )" +
                                                " then" +
                                                    " if ( redis.call( 'lindex', KEYS[2], 0 ) == ARGV[2] ) then" +
                                                        " if ( redis.call( 'llen', KEYS[2] ) <= 1 ) then" +
                                                            " redis.call( 'del', KEYS[1], KEYS[2] )" +
                                                            " return 1" +
                                                        " else" +
                                                            " redis.call( 'lpop', KEYS[2] )" +
                                                            " return 2" +
                                                        " end" +
                                                    " else " +
                                                        " return -2 " +
                                                    " end" +
                                                " else" +
                                                    " return -1" +
                                                " end";

    private static final String SORTED_REENTRANT_LOCK_SCRIPT =  " local val = redis.call( 'get', KEYS[1] )" +
                                                                " if ( val ) then" +
                                                                    " if ( val == ARGV[1] ) then" +
                                                                        " redis.call( 'lpush', KEYS[2], ARGV[3] ) )" +
                                                                        " if ( tonumber( ARGV[4] ) > 0 ) then" +
                                                                            " redis.call( 'pexpire', KEYS[1], ARGV[4] )" +
                                                                            " redis.call( 'pexpire', KEYS[2], ARGV[4] ) )" +
                                                                        " end"+
                                                                        " return 2" +
                                                                    " else" +
                                                                        " return -1" +
                                                                " else" +
                                                                    " redis.call( 'set' , KEYS[1] , ARGV[1], 'nx' , 'px' , ARGV[2] )" +
                                                                    " return 1" +
                                                                " end";

    private SortedReentrantLock() {}

    public SortedReentrantLock(String key, Object token, ObjectRedisService redisService, long expireMillis, boolean retry, long newExpireMillis) {
        this.key = key;
        this.token = token;
        this.redisService = redisService;
        this.expireMillis = expireMillis;
        this.retry = retry;
        this.newExpireMillis = newExpireMillis;
    }

    public Boolean tryLock(String owner) {
        if (StringUtils.isEmpty(owner)) {
            log.warn("Owner is empty.");
            return Boolean.FALSE;
        }

        long timeout = expireMillis; //Use expireMills as timeout.
        long start = System.currentTimeMillis();
        long end = start + timeout;

        while (true) {
            try {
                Long exeResult = redisService.execute(SORTED_REENTRANT_LOCK_SCRIPT, Long.class,
                        Arrays.asList(RedisKeyUtil.formatLockKey(key), RedisKeyUtil.formatLockKey(key + "_OWNERS")), token, expireMillis, owner, newExpireMillis);
                log.debug("The raw result of tryLock operation: {}(1: lock first time, 2: re-entrant, -1: fail to lock)", exeResult);
                if (exeResult != null && exeResult > 0) {
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

    public Boolean unlock(String owner) {
        if (StringUtils.isEmpty(owner)) {
            log.warn("Owner is empty.");
            return Boolean.FALSE;
        }
        if (!success) {
            return Boolean.FALSE;
        }

        long timeout = expireMillis; //Use expireMillis as timeout.
        long start = System.currentTimeMillis();
        long end = start + timeout;

        while (true) {
            try {
                Long exeResult = redisService.execute(UNLOCK_SCRIPT, Long.class,
                        Arrays.asList(RedisKeyUtil.formatLockKey(key), RedisKeyUtil.formatLockKey(key + "_OWNERS")), token, owner);
                log.debug("The raw result of tryUnlock operation: {}(1-all locks are released, 2-release one lock, 3-lock released by other or expired, -1-cannot match token, -2-cannot match owner)", exeResult);
                if (exeResult != null && exeResult > 0) {
                    return Boolean.TRUE;
                }
            } catch (Exception e) {
                log.error("Try to unlock {} with value {} unsuccessfully.", key, JacksonUtil.toJson(token), e);
            }

            if (timeout < RedisLockUtil.INTERVAL_TIME || System.currentTimeMillis() > end) {
                log.info("Try to unlock {} timeout.", key);
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

    public String getKey() {
        return key;
    }

    public Object getToken() {
        return token;
    }

}
