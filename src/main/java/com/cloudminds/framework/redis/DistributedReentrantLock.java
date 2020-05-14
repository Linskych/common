package com.cloudminds.framework.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @desc Usage scenario: Hold a lock for different applications
 * */
public class DistributedReentrantLock {

    private static final Logger log = LoggerFactory.getLogger(DistributedReentrantLock.class);

    private String key;//The key to be lock
    private Object token;//Set as value of the key
    private ObjectRedisService redisService;
    private long expireMills;//The lock last time in millSecond
    private boolean retry;//Retry when fail to lock or not
    private long newExpireMills;//Only for reentrant lock. This will be set as new expire time when re-entrant the lock.
    private Boolean success = Boolean.FALSE;;


    //Please do not remove any blank.
    private static final String UNLOCK_SCRIPT = " local val = redis.call( 'get', KEYS[1] )" +
                                                " if ( val == ARGV[1] ) then" +
                                                    " if ( redis.call( 'decr', KEYS[2] ) > 0 ) then" +
                                                        " return 2" +
                                                    " else " +
                                                        " redis.call( 'del', KEYS[1] )" +
                                                        " return 1 " +
                                                    " end" +
                                                " elseif ( val == false ) then" +
                                                    " return 3" +
                                                " else" +
                                                    " return -1" +
                                                " end";

    private static final String DIS_REENTRANT_LOCK_SCRIPT =   " if ( ARGV[1] == redis.get( 'get', KEYS[1] )) then" +
                                                                    " redis.call( 'incr', KEYS[2] ) )" +
                                                                    " if ( tonumber( ARGV[4] ) > 0 ) then"+
                                                                        " redis.call( 'pexpire', KEYS[1], ARGV[4] )"+
                                                                        " redis.call( 'pexpire', KEYS[2], ARGV[4] ) )" +
                                                                    " end"+
                                                                    " return 2" +
                                                                " elseif ( false != redis.call( 'set', KEYS[1] , ARGV[1], 'nx' , 'px' , ARGV[2] ) ) then" +
                                                                    " redis.call( 'incr', KEYS[2] ) )" +
                                                                    " redis.call( 'pexpire', KEYS[2], ARGV[2] ) )" +
                                                                    " return 1" +
                                                                " else" +
                                                                    " return -1" +
                                                                " end";

    private DistributedReentrantLock() {}

    public DistributedReentrantLock(String key, Object token, ObjectRedisService redisService, long expireMills, boolean retry, long newExpireMills) {
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
                //Check if the token is the same to the value of key. Lock successfully when yes or not exist the key.
                //If the newExpireMills is greater than zero, the key will be set new expire time as newExpireMills in millisecond
                Long exeResult = redisService.execute(DIS_REENTRANT_LOCK_SCRIPT, Long.class, Arrays.asList(RedisLockUtil.formatKey(key), RedisLockUtil.formatKey(key + "_COUNT")), token, expireMills, newExpireMills);
                log.debug("The raw result of tryLock operation: {}(1: lock successfully first time, 2: re-entrant, -1: fail to lock)", exeResult);
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

    public Boolean unlock() {
        if (!success) {
            return Boolean.FALSE;
        }
        try {
            //If there is only one holds this lock, we need to release this lock by deleting key-value from redis. Or we just need to decrease the locks.
            Long exeResult = redisService.execute(UNLOCK_SCRIPT, Long.class, Arrays.asList(RedisLockUtil.formatKey(key), RedisLockUtil.formatKey(key + "_COUNT")), token);
            log.debug("The raw result of tryUnlock operation: {}(1-del success, 2-not exist key, 0-fail to del)", exeResult);
            if (null != exeResult && exeResult > 0) {
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
