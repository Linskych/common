package com.cloudminds.framework.redis.lock;

import com.cloudminds.framework.redis.RedisLockUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DistributedReentrantLockTest {

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Test
    void tryLock() {
    }

    @Test
    void unlock() {
    }
}