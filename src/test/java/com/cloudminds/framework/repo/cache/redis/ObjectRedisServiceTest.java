package com.cloudminds.framework.repo.cache.redis;

import com.cloudminds.framework.json.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ObjectRedisServiceTest {

    @Autowired
    private ObjectRedisService redisService;

    private static final Logger log = LoggerFactory.getLogger(ObjectRedisServiceTest.class);

    @Test
    void set() {
        String key = "skych";
        Coder coder = new Coder("skych", 28);

        String key2 = "skych2";
        Coder coder2 = new Coder("skych2", 28);

        String val = "Lin Tianyu";
        long seconds = 30;
        redisService.setEx(key, coder, seconds);
        redisService.setEx(key2, coder2, seconds);

        List<Coder> list = redisService.get(Arrays.asList(key, key2), Coder.class);
        System.out.println(list.get(0));
    }

    @Test
    void get() {
        String key = "skych";
//        Coder coder = redisService.get(key, Coder.class);
        String val = redisService.get(key, String.class);
        System.out.println(val);
    }

    @Test
    void testGet() {
        int count = 10000000;
        long start = System.currentTimeMillis();
        int a = 0;
        Coder coder = new Coder("skych", 28);
        for (int i = 0; i < count; i++) {
            trytest(coder);
//            notrytest();
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    private void trytest(Coder coder) {
        try {
//            a++;

            JacksonUtil.toJson(coder);
//            Coder c = JacksonUtil.toObject(json, Coder.class);
//            throw new RuntimeException("try");
        } catch (Exception e) {
//            log.error("error.", e);
        }
    }

    private void notrytest() {
        Coder coder = new Coder("skych", 28);
        String json = JacksonUtil.toJson(coder);
        Coder c = JacksonUtil.toObject(json, Coder.class);
        throw new RuntimeException("try");
    }

    @Test
    void bitCount() {
        String key = "skych-bit";
        redisService.set(key, "Integer.valueOf(10)");
//        System.out.println(redisService.incr(key));
//        System.out.println(redisService.bitCount(key));
    }

    @Test
    void lpop() {
        String key = "lskych";
        Coder coder = new Coder("skych", 28);
        redisService.lpush(key, coder);
        Coder val = redisService.lpop(key, Coder.class);
        System.out.println(val);
    }
}