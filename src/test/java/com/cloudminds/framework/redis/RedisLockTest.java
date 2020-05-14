package com.cloudminds.framework.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisLockTest {

    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private ObjectRedisService redisService;

    @Test
    public void lock() {
        ReentrantLock lock = redisLockUtil.createReentrantLock("skych-re-entrant", 30000);
        ReentrantLock lock2 = redisLockUtil.createReentrantLock("skych-re-entrant", 3000);
        try{
//            System.out.println("get lock: " + lock.tryLock());
            System.out.println("get lock: " + lock.tryLock() + ", count: " + lock.getLocks());
            System.out.println("get lock: " + lock.tryLock() + ", count: " + lock.getLocks());
            System.out.println("get lock: " + lock.tryLock() + ", count: " + lock.getLocks());
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("release lock: " + lock.unlock() + ", count: " + lock.getLocks());
            System.out.println("release lock: " + lock.unlock() + ", count: " + lock.getLocks());
            System.out.println("release lock: " + lock.unlock() + ", count: " + lock.getLocks());
//            System.out.println("release lock: " + lock2.unlock());

        }
    }

    @Test
    public void sscan() {

//        redisService.set("skych", new Coder("Skych", 28));
//        redisService.expire("skych", 30);

//        Object bytes = null;
//
//        JacksonSerializerUtil.toList((byte[]) bytes, Coder.class);

//        String[] arr = new String[2];
//        arr[0] = JacksonUtil.toJson(new Coder("Skych", 28));
//        arr[1] = JacksonUtil.toJson(new Coder("Tianyu", 29));
//        redisService.sadd("skych", arr);
        redisService.sadd("skych", new Coder("Skych", 28), new Coder("Tianyu", 29));
//        redisService.sadd("skych", "Skych.lin", "Tianyu.lin","Skych.blue", "Tianyu.blue");
        List<Coder> list = redisService.sscan("skych", 10, Coder.class);
        System.out.println(list.get(0));
//        Set<Object> set = redisService.smembers("skych");
//        Set<String> set = redisService.smemberString("skych");
//        for (Object coder : set) {
//            Coder coder1 = (Coder)coder;
//            System.out.println(coder1);
//        }
    }

}
