package com.cloudminds.framework.redis;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class BaseRedisService<V> {

    private static final Logger log = LoggerFactory.getLogger(BaseRedisService.class);

    protected abstract RedisTemplate<String, V> getTemplate();

    public Boolean expire(String key, long seconds) {

        return getTemplate().expire(key, seconds, TimeUnit.SECONDS);
    }

    public Boolean exists(String key) {

        return getTemplate().hasKey(key);
    }

    public Boolean del(String key) {

        return getTemplate().delete(key);
    }

    public Long del(Collection<String> keys) {

        return getTemplate().delete(keys);
    }

    /**
     * @Note The result maybe not contain all keys because new keys can be set while scaning.
     * @param count Not total number of result. Count of each sacan operation. Set a reasonable value, not too small or too big.
     * */
    public List<String> scan(String keyPatter, long count) {
        if (StringUtils.isEmpty(keyPatter) || count <= 0) {
            return Collections.emptyList();
        }
        return getTemplate().execute((RedisCallback<List<String>>) con -> {
            List<String> keys = Lists.newArrayList();

            try (Cursor<byte[]> cursor = con.scan(ScanOptions.scanOptions().match(keyPatter).count(count).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (IOException e) {
                log.error("Execute scan occurs error.", e);
            }
            return keys;
        });
    }

    public Long incr(String key) {

        return getTemplate().opsForValue().increment(key);
    }

    public Long incrby(String key, long delta) {

        return getTemplate().opsForValue().increment(key, delta);
    }

    public Long decr(String key) {

        return getTemplate().opsForValue().decrement(key);
    }

    public Long decrby(String key, long delta) {

        return getTemplate().opsForValue().decrement(key, delta);
    }

    public Long hincrby(String key, String hash, long delta) {

        return getTemplate().opsForHash().increment(key, hash, delta);
    }

    public Boolean setBit(String key, long offset, boolean val) {

        return getTemplate().opsForValue().setBit(key, offset, val);
    }

    public Boolean getBit(String key, long offset) {

        return getTemplate().opsForValue().getBit(key, offset);
    }

    public Long bitCount(String key) {

        //The unit for start and end is byte, not bit. This is different from getBit and setBit operation.
        return getTemplate().execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes(StandardCharsets.UTF_8), 0L, -1L));
    }

    public Long hdel(String key, String hash) {

        return getTemplate().opsForHash().delete(key, hash);
    }

    public Long hdel(String key, List<String> hashKeys) {

        return getTemplate().opsForHash().delete(key, hashKeys.toArray(new String[0]));
    }

    public Boolean hexists(String key, String hash) {

        return getTemplate().opsForHash().hasKey(key, hash);
    }

    public Long hlen(String key) {

        return getTemplate().opsForHash().size(key);
    }

    public Long scard(String key) {

        return getTemplate().opsForSet().size(key);
    }
}
