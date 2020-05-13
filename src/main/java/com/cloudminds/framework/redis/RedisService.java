package com.cloudminds.framework.redis;

import com.cloudminds.framework.json.JacksonUtil;
import org.springframework.data.redis.core.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Note： Do not use this service in your business code.
 *       You should create cache manager service and set this service as a field.
 *       Business code use cache manager service.
 * */
@Component
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    /**
     * @param script Lua script to be executed in redis server
     * */
    public <T> T execute(String script, Class<T> returnType, List<String> keys, Object... values) {

        return (T) redisTemplate.execute(RedisScript.of(script, returnType), keys, values);
    }

    public Boolean expire(String key, long seconds) {

        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public Boolean exists(String key) {

        return redisTemplate.hasKey(key);
    }

    public Boolean del(String key) {

        return redisTemplate.delete(key);
    }

    public Long del(Collection<String> keys) {

        return redisTemplate.delete(keys);
    }

    /**
     * @Note The result maybe not contain all keys because new keys can be set while scaning.
     * @param count Not total number of result. Count of each sacan operation. Set a reasonable value, not too small or too big.
     * */
    public List<String> scan(String keyPatter, long count) {
        if (StringUtils.isEmpty(keyPatter) || count <= 0) {
            return Collections.emptyList();
        }
        return redisTemplate.execute((RedisCallback<List<String>>) con -> {
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

//===========================String=========================================
//cache data; counter; distributed lock; save object; online count(bit operation)

    public void set(String key, Object val, long seconds) {

        redisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    public <V> V get(String key, Class<V> type) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        return JacksonUtil.toObject((byte[]) obj, type);
    }

    public Object get(String key) {

        return redisTemplate.opsForValue().get(key);
    }

    public void set(Map<String, Object> kvMap) {

        redisTemplate.opsForValue().multiSet(kvMap);
    }

    public List<Object> get(Collection<String> keys) {

        return redisTemplate.opsForValue().multiGet(keys);
    }

    public Boolean setNxEx(String lockKey, Object value, long seconds) {
        if (value == null) {
            value = System.currentTimeMillis();
        }
        return redisTemplate.opsForValue().setIfAbsent(lockKey, value, seconds, TimeUnit.SECONDS);
    }

    public Boolean setNxPx(String lockKey, Object value, long mills) {
        if (value == null) {
            value = System.currentTimeMillis();
        }
        return redisTemplate.opsForValue().setIfAbsent(lockKey, value, mills, TimeUnit.MILLISECONDS);
    }

    public Long incr(String key) {

        return redisTemplate.opsForValue().increment(key);
    }

    public Long incrby(String key, long delta) {

        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Long decr(String key) {

        return redisTemplate.opsForValue().decrement(key);
    }

    public Long decrby(String key, long delta) {

        return redisTemplate.opsForValue().decrement(key, delta);
    }

    public Long hincrby(String key, String hash, long delta) {

        return redisTemplate.opsForHash().increment(key, hash, delta);
    }

    public Boolean setBit(String key, long offset, boolean val) {

        return redisTemplate.opsForValue().setBit(key, offset, val);
    }

    public Boolean getBit(String key, long offset) {

        return redisTemplate.opsForValue().getBit(key, offset);
    }

    public Long bitCount(String key) {

        //The unit for start and end is byte, not bit. This is different from getBit and setBit operation.
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes(StandardCharsets.UTF_8), 0L, -1L));
    }


//===========================List=========================================
//simple message queue; rank(history rank, not realtime); latest n items;

    public Long lpush(String key, Object val) {

        return redisTemplate.opsForList().leftPush(key, val);
    }

    public Object rpop(String key) {

        return redisTemplate.opsForList().rightPop(key);
    }

    public void rpush(String key, Object val) {

        redisTemplate.opsForList().rightPush(key, val);
    }

    public Object lpop(String key) {

        return redisTemplate.opsForList().leftPop(key);
    }

    public void lset(String key, long index, Object val) {

        redisTemplate.opsForList().set(key, index, val);
    }

    public Long llen(String key) {

        return redisTemplate.opsForList().size(key);
    }

    public List<Object> lrange(String key, long range) {

        return redisTemplate.opsForList().range(key, 0, range);
    }

    public void inStack(String stackName, Object val) {

        lpush(stackName, val);
    }

    public Object outStack(String stackName) {

        return lpop(stackName);
    }

    public void inQueue(String queue, Object val) {

        rpush(queue, val);
    }

    public Object outQueue(String queue) {

        return lpop(queue);
    }


//===========================Hash=========================================
//Save object which some fields will be modified; shopping car(user-item-num)


    public void hset(String key, String hash, Object val) {

        hashOperations.put(key, hash, val);
    }

    public void hmset(String key, Map<String, Object> kvMap) {

        hashOperations.putAll(key, kvMap);
    }

    public Object hget(String key, String hash) {

        return hashOperations.get(key, hash);
    }

    public List<Object> hmget(String key, Collection<String> hashKeys) {

        return hashOperations.multiGet(key, hashKeys);
    }

    public List<Object> hgetAll(String key) {

        return hashOperations.values(key);
    }

    public Long hdel(String key, String hash) {

        return hashOperations.delete(key, hash);
    }

    public Long hdel(String key, List<String> hashKeys) {

        return hashOperations.delete(key, hashKeys.toArray(new String[0]));
    }

    public Boolean hexists(String key, String hash) {

        return hashOperations.hasKey(key, hash);
    }

    public Long hlen(String key) {

        return hashOperations.size(key);
    }

    public Map<String, Object> hentries(String key) {

        return hashOperations.entries(key);
    }

    public Set<String> hkeys(String key) {

        return hashOperations.keys(key);
    }

    /**
     * @Note The result maybe not contain all entries because new entries can be put while scaning.
     * */
    public <T> Map<String, T> hscan(String key, String hashPattern, long count) {

        Map<String, T> map = Maps.newHashMap();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashPattern) || count <= 0) {
            return map;
        }

        try (Cursor<Map.Entry<String, Object>> cursor = hashOperations.scan(
                key, ScanOptions.scanOptions().match(hashPattern).count(count).build())) {

            while (cursor.hasNext()) {
                Map.Entry<String, Object> entry = cursor.next();
                map.put(entry.getKey(), (T) entry.getValue());
            }
        } catch (IOException e) {
            log.error("Execute hscan occurs error.", e);
        }

        return map;
    }

//===========================Set=========================================
//friends, like, interest, fans; random elements; black/white list;
// note: For multiple keys operations make sure that all keys are in the same slot.

    public Long sadd(String key, Object... vals) {

        return redisTemplate.opsForSet().add(key, vals);
    }

    public Long scard(String key) {

        return redisTemplate.opsForSet().size(key);
    }

    public Long srem(String key, Object val) {

        return redisTemplate.opsForSet().remove(key, val);
    }

    public Long srem(String key, List<Object> vals) {

        return redisTemplate.opsForSet().remove(key, vals.toArray());
    }

    public Set<Object> sdiff(String key, String otherKey) {

        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    public Set<Object> sdiff(String key, Collection<String> otherKeys) {

        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    public void sdiffStore(String key, String otherKey, String destKey) {

        redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    public void sdiffStore(String key, Collection<String> otherKeys, String destKey) {

        redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    public Set<Object> sinter(String key, String otherKey) {

        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    public Set<Object> sinter(Collection<String> keys) {

        return redisTemplate.opsForSet().intersect(keys);
    }

    public void sinterStore(String key, String otherKey, String destKey) {

        redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
    }

    public void sinterStore(Collection<String> keys, String destkey) {

        redisTemplate.opsForSet().intersectAndStore(keys, destkey);
    }

    public Boolean sismember(String key, Object target) {

        return redisTemplate.opsForSet().isMember(key, target);
    }

    public Set<Object> smembers(String key) {

        return redisTemplate.opsForSet().members(key);
    }

    public Object srandMember(String key) {

        return redisTemplate.opsForSet().randomMember(key);
    }

    public List<Object> srandMember(String key, long num) {

        return redisTemplate.opsForSet().randomMembers(key, num);
    }

    public Boolean smove(String key, Object val, String destKey) {

        return redisTemplate.opsForSet().move(key, val, destKey);
    }

    public Set<Object> sunion(String key, String otherKey) {

        return redisTemplate.opsForSet().union(key, otherKey);
    }

    public Set<Object> sunion(Collection<String> keys) {

        return redisTemplate.opsForSet().union(keys);
    }

    public void sunionStore(String key, String otherKey, String destKey) {

        redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    public void sunionStore(Collection<String> keys, String destKey) {

        redisTemplate.opsForSet().unionAndStore(keys, destKey);
    }

    /**
     * @Note The result maybe not contain all elements because new elements can be added while scaning.
     * */
    public <T> List<T> sscan(String key, long count) {

        List<T> list = Lists.newArrayList();
        if (StringUtils.isEmpty(key) || count < 0) {
            return list;
        }
        try (Cursor<T> cursor = (Cursor<T>) redisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(count).build())) {
            while (cursor.hasNext()) {
                list.add(cursor.next());
            }
        } catch (IOException e) {
            log.error("Execute sscan occurs error.", e);
        }

        return list;
    }


//===========================Zset=========================================
//realtime rank

    public Boolean zadd(String key, Object val, int score) {

        return redisTemplate.opsForZSet().add(key, val, score);
    }

    public Long zcard(String key) {

        return redisTemplate.opsForZSet().zCard(key);
    }

    public Long zcount(String key, int minScore, int maxScore) {

        return redisTemplate.opsForZSet().count(key, minScore, maxScore);
    }

    public Integer zincrby(String key, Object val, int delta) {

        Double score = redisTemplate.opsForZSet().incrementScore(key, val, delta);

        return score == null ? null : Integer.valueOf(score.toString());
    }

    public Integer zscore(String key, Object val) {

        Double score = redisTemplate.opsForZSet().score(key, val);

        return score == null ? null : Integer.valueOf(score.toString());
    }

    public Long zrank(String key, Object val) {

        return redisTemplate.opsForZSet().rank(key, val);
    }

    public Long zrevRank(String key, Object val) {

        return redisTemplate.opsForZSet().reverseRank(key, val);
    }

    public Long zrem(String key, Object val) {

        return redisTemplate.opsForZSet().remove(key, val);
    }

    public Long zrem(String key, List<Object> vals) {

        return redisTemplate.opsForZSet().remove(key, vals.toArray());
    }

    /**
     * order by score asc, alpha asc
     * */
    public Set<Object> zrange(String key, long start, long end) {

        return redisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zrangeWithScore(String key, long start, long end) {

        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * order by score desc, alpha desc
     * */
    public Set<Object> zrevRange(String key, long start, long end) {

        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zrevRangeWithScore(String key, long start, long end) {

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * order by score asc, alpha asc
     * */
    public Set<Object> zrangeMin(String key, long num) {

        return redisTemplate.opsForZSet().range(key, 0, num - 1);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zrangeMinWithScore(String key, long num) {

        return redisTemplate.opsForZSet().rangeWithScores(key, 0, num - 1);
    }

    /**
     * order by score desc, alpha desc
     * */
    public Set<Object> zrangeMax(String key, long num) {

        return redisTemplate.opsForZSet().reverseRange(key, 0, num - 1);
    }

    public Set<ZSetOperations.TypedTuple<Object>> zrangeMaxWithScore(String key, long num) {

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, num - 1);
    }

}
