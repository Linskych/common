package com.cloudminds.framework.redis;

import com.cloudminds.framework.json.JacksonUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    private static final Logger log = LoggerFactory.getLogger(RedisServiceTest.class);

    @Test
    void set() {
        String key = "skych";
        Coder coder = new Coder("skych", 28);
        String val = "Lin Tianyu";
        long seconds = 30;
        redisService.set(key, val, seconds);
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
}