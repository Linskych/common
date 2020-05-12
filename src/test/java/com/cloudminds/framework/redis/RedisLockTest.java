package com.cloudminds.framework.redis;

import com.cloudminds.framework.json.JacksonUtil;
import com.cloudminds.framework.redis.lock.RedisLockUtil;
import com.cloudminds.framework.redis.lock.ReentrantLock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisLockTest {

    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private RedisService redisService;

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

    @Test
    public void sscan() {
//        String[] arr = new String[2];
//        arr[0] = JacksonUtil.toJson(new Coder("Skych", 28));
//        arr[1] = JacksonUtil.toJson(new Coder("Tianyu", 29));
//        redisService.sadd("skych", arr);
        redisService.sadd("skych", new Coder("Skych", 28), new Coder("Tianyu", 29));
//        redisService.sadd("skych", "Skych.lin", "Tianyu.lin","Skych.blue", "Tianyu.blue");
//        List<Coder> list = redisService.sscan("skych", 10);
        Set<Object> set = redisService.smembers("skych");
//        Set<String> set = redisService.smemberString("skych");
        for (Object coder : set) {
            Coder coder1 = (Coder)coder;
            System.out.println(coder1);
        }
    }

}
