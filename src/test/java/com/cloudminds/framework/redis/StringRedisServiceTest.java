package com.cloudminds.framework.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StringRedisServiceTest {

    @Autowired
    private StringRedisService stringRedisService;

    @Test
    void hset() {
        String key = "skych";
        String hash = "lin";
        String val = "林天瑜";
        stringRedisService.hset(key, hash, val);
        System.out.println(stringRedisService.hget(key,hash));
    }

    @Test
    void set() {

        String key = "skych";
        String hash = "lin";
        String val = "a";
        stringRedisService.set(key, val);
        System.out.println(stringRedisService.bitCount(key));
    }
}