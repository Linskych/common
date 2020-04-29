package com.cloudminds.framework.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * @param script lua script to be executed in redis server
     * */
    public <T> T execute(String script, Class<T> returnType, List<String> keys, String... values) {

        return (T) redisTemplate.execute(RedisScript.of(script, returnType), keys, values);
    }

    public Boolean expire(String key, long seconds) {

        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public Boolean exists(final String key) {

        return redisTemplate.hasKey(key);
    }

    public void set(String key, Object val, long seconds) {

        redisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    public Object get(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    public Boolean del(String key) {

        return redisTemplate.delete(key);
    }

    public void mset(Map<String, Object> kvMap) {

        redisTemplate.opsForValue().multiSet(kvMap);
    }

    public List<Object> mget(Collection<String> keys) {

        return redisTemplate.opsForValue().multiGet(keys);
    }

    public Long mdel(Collection<String> keys) {

        return redisTemplate.delete(keys);
    }

    public Boolean setNxEx(String lockKey, Object value, long seconds) {
        if (value == null) {
            value = System.currentTimeMillis();
        }
        return redisTemplate.opsForValue().setIfAbsent(lockKey, value, seconds, TimeUnit.SECONDS);
    }

}
