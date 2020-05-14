package com.cloudminds.framework.redis;

import com.google.common.collect.Sets;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.util.CollectionUtils;

/**
 * Note： Do not use this service in your business code.
 *       You should create cache manager service and set this service as a field.
 *       Business code use cache manager service.
 * */
@Component
public class ObjectRedisService extends BaseRedisService<Object> {

    private static final Logger log = LoggerFactory.getLogger(ObjectRedisService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Qualifier("hashOperations")
    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Override
    protected RedisTemplate getTemplate() {
        return redisTemplate;
    }

    /**
     * @param script Lua script to be executed in redis server
     * */
    public <T> T execute(String script, Class<T> returnType, List<String> keys, Object... values) {

        return (T) redisTemplate.execute(RedisScript.of(script, returnType), keys, values);
    }

//===========================String=========================================
//cache data; counter; distributed lock; save object; online count(bit operation)

    public void set(String key, Object val) {

        redisTemplate.opsForValue().set(key, val);
    }

    public void setEx(String key, Object val, long seconds) {
        if (seconds <= 0) {
            throw new RuntimeException("Expire time can not be less than 0.");
        }
        redisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    public <V> V get(String key, Class<V> type) {

        Object obj = redisTemplate.opsForValue().get(key);

        return deserializeObject((byte[]) obj, type);
    }

    public void set(Map<String, Object> kvMap) {

        redisTemplate.opsForValue().multiSet(kvMap);
    }

    public <T> List<T> get(Collection<String> keys, Class<T> type) {

        List<Object> list = redisTemplate.opsForValue().multiGet(keys);

        return deserializeList(list, type);
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


//===========================List=========================================
//simple message queue; rank(history rank, not realtime); latest n items;

    public Long lpush(String key, Object val) {

        return redisTemplate.opsForList().leftPush(key, val);
    }

    public <V> V rpop(String key, Class<V> type) {

        Object val = redisTemplate.opsForList().rightPop(key);

        return deserializeObject((byte[]) val, type);
    }

    public void rpush(String key, Object val) {

        redisTemplate.opsForList().rightPush(key, val);
    }

    public <V> V lpop(String key, Class<V> type) {

        Object val = redisTemplate.opsForList().leftPop(key);

        return deserializeObject((byte[]) val, type);
    }

    public void lset(String key, long index, Object val) {

        redisTemplate.opsForList().set(key, index, val);
    }

    public Long llen(String key) {

        return redisTemplate.opsForList().size(key);
    }

    public <V> List<V> lrange(String key, long range, Class<V> type) {

        List<Object> list =redisTemplate.opsForList().range(key, 0, range);

        return deserializeList(list, type);
    }

    public void inStack(String stackName, Object val) {

        lpush(stackName, val);
    }

    public <V> V outStack(String stackName, Class<V> type) {

        return lpop(stackName, type);
    }

    public void inQueue(String queue, Object val) {

        rpush(queue, val);
    }

    public <V> V outQueue(String queue, Class<V> type) {

        return lpop(queue, type);
    }


//===========================Hash=========================================
//Save object which some fields will be modified; shopping car(user-item-num)


    public void hset(String key, String hash, Object val) {

        hashOperations.put(key, hash, val);
    }

    public void hmset(String key, Map<String, Object> kvMap) {

        hashOperations.putAll(key, kvMap);
    }

    public <V> V hget(String key, String hash, Class<V> type) {

        Object val = hashOperations.get(key, hash);

        return deserializeObject((byte[]) val, type);
    }

    public <V> List<V> hmget(String key, Collection<String> hashKeys, Class<V> type) {

        List<Object> list = hashOperations.multiGet(key, hashKeys);

        return deserializeList(list, type);
    }

    public <V> List<V> hgetAll(String key, Class<V> type) {

        List<Object> list = hashOperations.values(key);

        return deserializeList(list, type);
    }

    public <V> Map<String, V> hentries(String key, Class<V> type) {

        Map<String, Object> map = hashOperations.entries(key);
        if (CollectionUtils.isEmpty(map)) {
            return Collections.emptyMap();
        }

        Map<String, V> values = Maps.newHashMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            values.put(entry.getKey(), deserializeObject((byte[]) entry.getValue(), type));
        }

        return values;
    }

    public Set<String> hkeys(String key) {

        return hashOperations.keys(key);
    }

    /**
     * @Note The result maybe not contain all entries because new entries can be put while scaning.
     * */
    public <V> Map<String, V> hscan(String key, String hashPattern, long count, Class<V> type) {

        Map<String, V> map = Maps.newHashMap();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashPattern) || count <= 0) {
            return map;
        }
        try (Cursor<Map.Entry<String, Object>> cursor = hashOperations.scan(
                key, ScanOptions.scanOptions().match(hashPattern).count(count).build())) {

            while (cursor.hasNext()) {
                Map.Entry<String, Object> entry = cursor.next();
                map.put(entry.getKey(), deserializeObject((byte[]) entry.getValue(), type));
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

    public Long srem(String key, Object val) {

        return redisTemplate.opsForSet().remove(key, val);
    }

    public Long srem(String key, List<Object> vals) {

        return redisTemplate.opsForSet().remove(key, vals.toArray());
    }

    public <V> Set<V> sdiff(String key, String otherKey, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().difference(key, otherKey);

        return deserializeSet(set, type);
    }

    public <V> Set<V> sdiff(String key, Collection<String> otherKeys, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().difference(key, otherKeys);

        return deserializeSet(set, type);
    }

    public Long sdiffStore(String key, String otherKey, String destKey) {

        return redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    public Long sdiffStore(String key, Collection<String> otherKeys, String destKey) {

        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    public <V> Set<V> sinter(String key, String otherKey, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().intersect(key, otherKey);

        return deserializeSet(set, type);
    }

    public <V> Set<V> sinter(Collection<String> keys, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().intersect(keys);

        return deserializeSet(set, type);
    }

    public Long sinterStore(String key, String otherKey, String destKey) {

        return redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
    }

    public Long sinterStore(Collection<String> keys, String destkey) {

        return redisTemplate.opsForSet().intersectAndStore(keys, destkey);
    }

    public Boolean sismember(String key, Object target) {

        return redisTemplate.opsForSet().isMember(key, target);
    }

    public <V> Set<V> smembers(String key, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().members(key);

        return deserializeSet(set, type);
    }

    public <V> V srandMember(String key, Class<V> type) {

        Object val = redisTemplate.opsForSet().randomMember(key);

        return deserializeObject((byte[]) val, type);
    }

    public <V> List<V> srandMember(String key, long num, Class<V> type) {

        List<Object> list = redisTemplate.opsForSet().randomMembers(key, num);

        return deserializeList(list, type);
    }

    public Boolean smove(String key, Object val, String destKey) {

        return redisTemplate.opsForSet().move(key, val, destKey);
    }

    public <V> Set<V> sunion(String key, String otherKey, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().union(key, otherKey);

        return deserializeSet(set, type);
    }

    public <V> Set<V> sunion(Collection<String> keys, Class<V> type) {

        Set<Object> set = redisTemplate.opsForSet().union(keys);

        return deserializeSet(set, type);
    }

    public Long sunionStore(String key, String otherKey, String destKey) {

        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    public Long sunionStore(Collection<String> keys, String destKey) {

        return redisTemplate.opsForSet().unionAndStore(keys, destKey);
    }

    /**
     * @Note The result maybe not contain all elements because new elements can be added while scaning.
     * */
    public <V> List<V> sscan(String key, long count, Class<V> type) {

        List<V> list = Lists.newArrayList();
        if (StringUtils.isEmpty(key) || count < 0) {
            return list;
        }
        try (Cursor<Object> cursor = redisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(count).build())) {
            while (cursor.hasNext()) {
                list.add(deserializeObject((byte[]) cursor.next(), type));
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
    public <V> Set<V> zrange(String key, long start, long end, Class<V> type) {

        Set<Object> set = redisTemplate.opsForZSet().range(key, start, end);

        return deserializeSet(set, type);
    }

    public <V> Set<ZSetOperations.TypedTuple<V>> zrangeWithScore(String key, long start, long end, Class<V> type) {

        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().rangeWithScores(key, start, end);

        return deserializeTupleSet(set, type);
    }

    /**
     * order by score desc, alpha desc
     * */
    public <V> Set<V> zrevRange(String key, long start, long end, Class<V> type) {

        Set<Object> set = redisTemplate.opsForZSet().reverseRange(key, start, end);

        return deserializeSet(set, type);
    }

    public <V> Set<ZSetOperations.TypedTuple<V>> zrevRangeWithScore(String key, long start, long end, Class<V> type) {

        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

        return deserializeTupleSet(set, type);
    }

    /**
     * order by score asc, alpha asc
     * */
    public <V> Set<V> zrangeAsc(String key, long num, Class<V> type) {

        Set<Object> set = redisTemplate.opsForZSet().range(key, 0, num - 1);

        return deserializeSet(set, type);
    }

    public <V> Set<ZSetOperations.TypedTuple<V>> zrangeAscWithScore(String key, long num, Class<V> type) {

        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().rangeWithScores(key, 0, num - 1);

        return deserializeTupleSet(set, type);
    }

    /**
     * order by score desc, alpha desc
     * */
    public <V> Set<V> zrangeDesc(String key, long num, Class<V> type) {

        Set<Object> set = redisTemplate.opsForZSet().reverseRange(key, 0, num - 1);

        return deserializeSet(set, type);
    }

    public <V> Set<ZSetOperations.TypedTuple<V>> zrangeDescWithScore(String key, long num, Class<V> type) {

        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, num - 1);

        return deserializeTupleSet(set, type);
    }



    private <V> Set<ZSetOperations.TypedTuple<V>> deserializeTupleSet(Set<ZSetOperations.TypedTuple<Object>> set, Class<V> type) {
        if (CollectionUtils.isEmpty(set)) {
            return Collections.emptySet();
        }

        Set<ZSetOperations.TypedTuple<V>> values = Sets.newHashSet();
        set.forEach(tuple -> values.add(new DefaultTypedTuple<>(deserializeObject((byte[]) tuple.getValue(), type), tuple.getScore())));

        return values;
    }

    private <V> Set<V> deserializeSet(Set<Object> set, Class<V> type) {
        if (CollectionUtils.isEmpty(set)) {
            return Collections.emptySet();
        }

        Set<V> values = Sets.newHashSet();
        set.forEach(obj -> values.add(deserializeObject((byte[]) obj, type)));

        return values;
    }

    private <V> List<V> deserializeList(List<Object> list, Class<V> type) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<V> values = Lists.newArrayList();
        list.forEach(obj -> values.add(deserializeObject((byte[]) obj, type)));

        return values;
    }

    private <V> V deserializeObject(byte[] val, Class<V> type) {

        return JacksonSerializerUtil.toObject((byte[]) val, type);
    }

}
