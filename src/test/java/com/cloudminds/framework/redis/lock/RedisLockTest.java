package com.cloudminds.framework.redis.lock;

import com.cloudminds.framework.json.JacksonUtil;
import com.cloudminds.framework.redis.Coder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLockTest {

    @Autowired
    private RedisLockUtil redisLockUtil;


    @Test
    void tryLock() {

        RedisLock redisLock = redisLockUtil.createLock("skych", "emma", 30000, true);
        System.out.println(redisLock.tryLock());
    }

    @Test
    void unlock() {
        Coder coder = get(Coder.class);
        System.out.println(coder);
    }

    private <V> V get(Class<V> type) {
        String json = JacksonUtil.toJson(new Coder("skych", 28));
        V coder = JacksonUtil.toObject(json, type);
        return coder;
    }
}