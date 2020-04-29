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

//TODO 1)lock multiple keys; 2)reentry lock
@Component
public class RedisLock {

    private static final Logger log = LoggerFactory.getLogger(RedisLock.class);

    //Please do not remove any blank.
    private static final String LUA_SCRIPT = "local val = redis.call( 'get', KEYS[1] )" +
                                             " if ( val == false )" +
                                             " then" +
                                                " return 2" +
                                             " elseif ( val == ARGV[1] )" +
                                             " then" +
                                                " return redis.call( 'del', KEYS[1] )"+
                                             " else" +
                                                " return 0" +
                                             " end";

    private static final String LOCK_KEY_PREFIX = "REDIS_LOCK";

    @Autowired
    private RedisService redisService;
    @Autowired
    @Qualifier("uuidGenerator")
    private SerialNumGenerator serialNumGenerator;

    /**
     * @return An empty if do not get lock, or an uuid which was set as value of lockKey and this uuid will be used when tryUnlock lockKey.
     * */
    public String tryLock(int retries, String lockKey, long seconds) {
        if (StringUtils.isEmpty(lockKey)) {
            log.warn("LockKey MUST NOT be empty.");
            return StringUtils.EMPTY;
        }
        if (seconds <= 0) {
            log.warn("Expire time MUST be more than zero.");
            return StringUtils.EMPTY;
        }
        String value = serialNumGenerator.getSerialNum();
        int times = 0;
        while (times <= retries) {
            try {
                boolean lock = redisService.setNxEx(LOCK_KEY_PREFIX  + lockKey, value, seconds);
                if (lock) {
                    return value;
                }
                times++;
            } catch (Exception e) {
                log.error("Try {} times to get redis lock error.", times + 1, e);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @return An empty if do not get lock, or an uuid which was set as value of lockKey and this uuid will be used when tryUnlock lockKey.
     * */
    public String tryLock(String lockKey, long seconds) {

        return tryLock(0, lockKey, seconds);
    }

    public boolean tryUnlock(String lockKey, String uniqueLockValue) {

        if (StringUtils.isEmpty(lockKey) || StringUtils.isEmpty(uniqueLockValue)) {
            log.warn("LockKey and uniqueLockValue are both necessary.");
            return false;
        }
        try {
            Long exeResult = redisService.execute(LUA_SCRIPT, Long.class, Collections.singletonList(LOCK_KEY_PREFIX + lockKey), uniqueLockValue);
            if (null != exeResult && exeResult >= 1) {
                log.debug("The raw result of tryUnlock option: {}(1-del success, 2-not exist key, 0-fail to del or val not match)", exeResult);
                return true;
            }
        } catch (Exception e) {
            log.error("Try to unlock {} with value {} unsuccessfully.", lockKey, uniqueLockValue, e);
            return false;
        }
        return false;
    }
}
