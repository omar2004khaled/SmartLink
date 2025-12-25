package com.example.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public void setHashValue(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public Object getHashValue(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Map<Object, Object> getAllHashEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Long deleteHashKey(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    // ============== List Operations ==============

    public Long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public Long leftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public List<Object> getListRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ============== Set Operations ==============

    public Long addToSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean isMemberOfSet(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    // ============== Sorted Set Operations ==============

    public Boolean addToSortedSet(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public Set<Object> getSortedSetByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * Get members from sorted set by range in descending order (highest score first)
     * @param key the Redis key
     * @param start start index (0-based)
     * @param end end index inclusive
     * @return set of members in descending score order
     */
    public Set<Object> getSortedSetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    public Double getSortedSetScore(String key, Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    public Long removeSortedSetMembers(String key, Object... members) {
        return redisTemplate.opsForZSet().remove(key, members);
    }

    /**
     * Maintain LRU list for feed keys and set TTL
     * @param feedKey the feed key (e.g., "feed:123")
     * @param maxKeys max number of keys to keep in LRU list
     * @param ttlSeconds time to live in seconds
     */
    public void maintainFeedKeyLRU(String feedKey, int maxKeys, long ttlSeconds) {
        try {
            String lruListKey = "feed_keys_lru";
            redisTemplate.opsForList().leftPush(lruListKey, feedKey);
            redisTemplate.opsForList().trim(lruListKey, 0, maxKeys - 1);
            expire(feedKey, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("LRU maintenance error: " + e.getMessage());
        }
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}