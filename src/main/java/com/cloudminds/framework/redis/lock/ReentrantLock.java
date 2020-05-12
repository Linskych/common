package com.cloudminds.framework.redis.lock;

import com.cloudminds.framework.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @desc Usage scenario: Held a lock by a thread and its sub threads(A lock no need to share with different applications)
 * */
public class ReentrantLock {

    private static final Logger log = LoggerFactory.getLogger(ReentrantLock.class);

    private String key;//The key to be lock
    private String token;//Set as value of the key
    private RedisService redisService;
    private long expireMills;//The lock last time in millSecond
    private boolean retry;//Retry when fail to lock or not
    private long newExpireMills;//Only for reentrant lock. This will be set as new expire time when re-entrant the lock.
    private final AtomicInteger locks = new AtomicInteger();//To indicate how many times this lock used. This will be useful when unlock key.
    private Boolean success;


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

    private static final int REENTRANT_SUCCESS = 1;
    private static final String REENTRANT_LOCK_SCRIPT = " if ( ARGV[1] == redis.call( 'get', KEYS[1] ) ) then" +
                                                            " if ( tonumber( ARGV[2] ) > 0 ) then"+
                                                                " redis.call( 'expire', KEYS[1], ARGV[2] )"+
                                                            " end"+
                                                            " return 1" +
                                                        " else" +
                                                            " return -1" +
                                                        " end";

    private ReentrantLock() {}

    public ReentrantLock(String key, String token, RedisService redisService, long expireMills, boolean retry, long newExpireMills) {
        this.key = key;
        this.token = token;
        this.redisService = redisService;
        this.expireMills = expireMills;
        this.retry = retry;
        this.newExpireMills = newExpireMills;
    }

    public Boolean tryLock() {

        long timeout = expireMills; //Use expireMills as timeout.
        long start = System.currentTimeMillis();
        long end = start + timeout;

        while (true) {
            try {
                if (locks.intValue() > 0) {
                    //It is the second time or after to try to lock.
                    //Check if the token is the same to the value of key. Lock successfully when yes.
                    //If the newExpireMills is greater than zero, the key will be set new expire time as newExpireMills in millisecond
                    Long exeResult = redisService.execute(REENTRANT_LOCK_SCRIPT, Long.class, Collections.singletonList(RedisLockUtil.formatKey(key)), token, newExpireMills);
                    if (exeResult != null && exeResult > 0) {
                        locks.incrementAndGet();
                        return Boolean.TRUE;
                    }
                } else {
                    //It is the first time to try to lock.
                    boolean lock = redisService.setNxPx(RedisLockUtil.formatKey(key), token, expireMills);
                    if (lock) {
                        locks.incrementAndGet();
                        return success = Boolean.TRUE;
                    }
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

    public Boolean unlock() {
        if (!success) {
            //Never get lock.
            return Boolean.FALSE;
        }
        //If there is only one holds this lock, we need to release this lock by deleting key-value from redis. Or we just need to decrease the locks.
        if (locks.decrementAndGet() > 0) {
            return Boolean.TRUE;
        }
        try {
            Long exeResult = redisService.execute(UNLOCK_SCRIPT, Long.class, Collections.singletonList(RedisLockUtil.formatKey(key)), token);
            log.debug("The raw result of tryUnlock operation: {}(1-del success, 2-not exist key, 0-fail to del)", exeResult);
            if (exeResult != null && exeResult > 0) {
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
