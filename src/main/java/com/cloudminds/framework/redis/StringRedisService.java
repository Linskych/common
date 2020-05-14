package com.cloudminds.framework.redis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class StringRedisService extends BaseRedisService<String> {

    private static final Logger log = LoggerFactory.getLogger(StringRedisService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Qualifier("stringHashOperations")
    @Autowired
    private HashOperations<String, String, String> stringHashOperations;


    @Override
    protected RedisTemplate getTemplate() {
        return stringRedisTemplate;
    }

    /**
     * @param script Lua script to be executed in redis server
     * */
    public <T> T execute(String script, Class<T> returnType, List<String> keys, String... values) {

        return (T) stringRedisTemplate.execute(RedisScript.of(script, returnType), keys, values);
    }

//===========================String=========================================
//cache data; counter; distributed lock; save object; online count(bit operation)

    public void set(String key, String val) {

        stringRedisTemplate.opsForValue().set(key, val);
    }

    public void setEx(String key, String val, long seconds) {
        if (seconds <= 0) {
            throw new RuntimeException("Expire time can not be less than 0.");
        }
        stringRedisTemplate.opsForValue().set(key, val, seconds, TimeUnit.SECONDS);
    }

    public String get(String key) {

        return stringRedisTemplate.opsForValue().get(key);
    }

    public void set(Map<String, String> kvMap) {

        stringRedisTemplate.opsForValue().multiSet(kvMap);
    }

    public List<String> get(Collection<String> keys) {

        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    public Boolean setNxEx(String key, String val, long seconds) {
        if (StringUtils.isEmpty(val)) {
            val = String.valueOf(System.currentTimeMillis());
        }
        return stringRedisTemplate.opsForValue().setIfAbsent(key, val, seconds, TimeUnit.SECONDS);
    }

    public Boolean setNxPx(String lockKey, String val, long mills) {
        if (StringUtils.isEmpty(val)) {
            val = String.valueOf(System.currentTimeMillis());
        }
        return stringRedisTemplate.opsForValue().setIfAbsent(lockKey, val, mills, TimeUnit.MILLISECONDS);
    }


//===========================List=========================================
//simple message queue; rank(history rank, not realtime); latest n items;

    public Long lpush(String key, String val) {

        return stringRedisTemplate.opsForList().leftPush(key, val);
    }

    public String rpop(String key) {

        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public void rpush(String key, String val) {

        stringRedisTemplate.opsForList().rightPush(key, val);
    }

    public String lpop(String key) {

        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public void lset(String key, long index, String val) {

        stringRedisTemplate.opsForList().set(key, index, val);
    }

    public Long llen(String key) {

        return stringRedisTemplate.opsForList().size(key);
    }

    public List<String> lrange(String key, long range) {

        return stringRedisTemplate.opsForList().range(key, 0, range);
    }

    public void inStack(String stackName, String val) {

        lpush(stackName, val);
    }

    public String outStack(String stackName) {

        return lpop(stackName);
    }

    public void inQueue(String queue, String val) {

        rpush(queue, val);
    }

    public String outQueue(String queue) {

        return lpop(queue);
    }


//===========================Hash=========================================
//Save object which some fields will be modified; shopping car(user-item-num)


    public void hset(String key, String hash, String val) {

        stringHashOperations.put(key, hash, val);
    }

    public void hmset(String key, Map<String, String> kvMap) {

        stringHashOperations.putAll(key, kvMap);
    }

    public String hget(String key, String hash) {

        return stringHashOperations.get(key, hash);
    }

    public List<String> hmget(String key, Collection<String> hashKeys) {

        return stringHashOperations.multiGet(key, hashKeys);
    }

    public List<String> hgetAll(String key) {

        return stringHashOperations.values(key);
    }

    public Set<String> hkeys(String key) {

        return stringHashOperations.keys(key);
    }

    public Map<String, String> hentries(String key) {

        return stringHashOperations.entries(key);
    }

    /**
     * @Note The result maybe not contain all entries because new entries can be put while scaning.
     * */
    public Map<String, String> hscan(String key, String hashPattern, long count) {

        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(hashPattern) || count <= 0) {
            return map;
        }

        try (Cursor<Map.Entry<String, String>> cursor = stringHashOperations.scan(
                key, ScanOptions.scanOptions().match(hashPattern).count(count).build())) {

            while (cursor.hasNext()) {
                Map.Entry<String, String> entry = cursor.next();
                map.put(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            log.error("Execute hscan occurs error.", e);
        }

        return map;
    }

//===========================Set=========================================
//friends, like, interest, fans; random elements; black/white list;
// note: For multiple keys operations make sure that all keys are in the same slot.

    public Long sadd(String key, String... vals) {

        return stringRedisTemplate.opsForSet().add(key, vals);
    }

    public Long srem(String key, String val) {

        return stringRedisTemplate.opsForSet().remove(key, val);
    }

    public Long srem(String key, List<String> vals) {

        return stringRedisTemplate.opsForSet().remove(key, vals.toArray(new String[0]));
    }

    public Set<String> sdiff(String key, String otherKey) {

        return stringRedisTemplate.opsForSet().difference(key, otherKey);
    }

    public Set<String> sdiff(String key, Collection<String> otherKeys) {

        return stringRedisTemplate.opsForSet().difference(key, otherKeys);
    }

    public Long sdiffStore(String key, String otherKey, String destKey) {

        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
    }

    public Long sdiffStore(String key, Collection<String> otherKeys, String destKey) {

        return stringRedisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey);
    }

    public Set<String> sinter(String key, String otherKey) {

        return stringRedisTemplate.opsForSet().intersect(key, otherKey);
    }

    public Set<String> sinter(Collection<String> keys) {

        return stringRedisTemplate.opsForSet().intersect(keys);
    }

    public Long sinterStore(String key, String otherKey, String destKey) {

        return stringRedisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
    }

    public Long sinterStore(Collection<String> keys, String destkey) {

        return stringRedisTemplate.opsForSet().intersectAndStore(keys, destkey);
    }

    public Boolean sismember(String key, String target) {

        return stringRedisTemplate.opsForSet().isMember(key, target);
    }

    public Set<String> smembers(String key) {

        return stringRedisTemplate.opsForSet().members(key);
    }

    public String srandMember(String key) {

        return stringRedisTemplate.opsForSet().randomMember(key);
    }

    public List<String> srandMember(String key, long num) {

        return stringRedisTemplate.opsForSet().randomMembers(key, num);
    }

    public Boolean smove(String key, String val, String destKey) {

        return stringRedisTemplate.opsForSet().move(key, val, destKey);
    }

    public Set<String> sunion(String key, String otherKey) {

        return stringRedisTemplate.opsForSet().union(key, otherKey);
    }

    public Set<String> sunion(Collection<String> keys) {

        return stringRedisTemplate.opsForSet().union(keys);
    }

    public Long sunionStore(String key, String otherKey, String destKey) {

        return stringRedisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    public Long sunionStore(Collection<String> keys, String destKey) {

        return stringRedisTemplate.opsForSet().unionAndStore(keys, destKey);
    }

    /**
     * @Note The result maybe not contain all elements because new elements can be added while scaning.
     * */
    public List<String> sscan(String key, long count) {

        List<String> list = Lists.newArrayList();
        if (StringUtils.isEmpty(key) || count < 0) {
            return list;
        }
        try (Cursor<String> cursor = stringRedisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(count).build())) {
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

    public Boolean zadd(String key, String val, int score) {

        return stringRedisTemplate.opsForZSet().add(key, val, score);
    }

    public Long zcard(String key) {

        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    public Long zcount(String key, int minScore, int maxScore) {

        return stringRedisTemplate.opsForZSet().count(key, minScore, maxScore);
    }

    public Integer zincrby(String key, String val, int delta) {

        Double score = stringRedisTemplate.opsForZSet().incrementScore(key, val, delta);

        return score == null ? null : Integer.valueOf(score.toString());
    }

    public Integer zscore(String key, String val) {

        Double score = stringRedisTemplate.opsForZSet().score(key, val);

        return score == null ? null : Integer.valueOf(score.toString());
    }

    public Long zrank(String key, String val) {

        return stringRedisTemplate.opsForZSet().rank(key, val);
    }

    public Long zrevRank(String key, String val) {

        return stringRedisTemplate.opsForZSet().reverseRank(key, val);
    }

    public Long zrem(String key, String val) {

        return stringRedisTemplate.opsForZSet().remove(key, val);
    }

    public Long zrem(String key, List<String> vals) {

        return stringRedisTemplate.opsForZSet().remove(key, vals.toArray(new String[0]));
    }

    /**
     * order by score asc, alpha asc
     * */
    public Set<String> zrange(String key, long start, long end) {

        return stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<ZSetOperations.TypedTuple<String>> zrangeWithScore(String key, long start, long end) {

        return stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * order by score desc, alpha desc
     * */
    public Set<String> zrevRange(String key, long start, long end) {

        return stringRedisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public Set<ZSetOperations.TypedTuple<String>> zrevRangeWithScore(String key, long start, long end) {

        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    /**
     * order by score asc, alpha asc
     * */
    public Set<String> zrangeAsc(String key, long num) {

        return stringRedisTemplate.opsForZSet().range(key, 0, num - 1);
    }

    public Set<ZSetOperations.TypedTuple<String>> zrangeAscWithScore(String key, long num) {

        return stringRedisTemplate.opsForZSet().rangeWithScores(key, 0, num - 1);
    }

    /**
     * order by score desc, alpha desc
     * */
    public Set<String> zrangeDesc(String key, long num) {

        return stringRedisTemplate.opsForZSet().reverseRange(key, 0, num - 1);
    }

    public Set<ZSetOperations.TypedTuple<String>> zrangeDescWithScore(String key, long num) {

        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, num - 1);
    }
}
