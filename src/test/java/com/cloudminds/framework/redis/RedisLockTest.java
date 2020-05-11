package com.cloudminds.framework.redis;

import com.cloudminds.framework.redis.lock.RedisLockUtil;
import com.cloudminds.framework.redis.lock.ReentrantLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisLockTest {

    @Autowired
    private RedisLockUtil redisLockUtil;

    @Test
    public void lock() {
        ReentrantLock lock = redisLockUtil.createReentrantLock("skych-re-entrant", 3000);
        ReentrantLock lock2 = redisLockUtil.createReentrantLock("skych-re-entrant", 3000);
        try{
            System.out.println("get lock: " + lock.tryLock());
//            System.out.println("get lock: " + lock.tryLock() + ", count: " + lock.getLocks());
//            System.out.println("get lock: " + lock.tryLock() + ", count: " + lock.getLocks());
//            System.out.println("get lock: " + lock.tryLock() + ", count: " + lock.getLocks());
//            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
//            System.out.println("release lock: " + lock.unlock() + ", count: " + lock.getLocks());
//            System.out.println("release lock: " + lock.unlock() + ", count: " + lock.getLocks());
//            System.out.println("release lock: " + lock.unlock() + ", count: " + lock.getLocks());
            System.out.println("release lock: " + lock2.unlock());

        }
    }
}
