package com.cloudminds.framework.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

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
    }
}