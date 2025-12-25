package com.example.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void testSetValue() {
        String key = "testKey";
        String value = "testValue";

        redisService.setValue(key, value);

        verify(valueOperations).set(key, value);
    }

    @Test
    void testSetValueWithTimeout() {
        String key = "testKey";
        String value = "testValue";
        long timeout = 60L;
        TimeUnit unit = TimeUnit.SECONDS;

        redisService.setValue(key, value, timeout, unit);

        verify(valueOperations).set(key, value, timeout, unit);
    }

    @Test
    void testGetValue() {
        String key = "testKey";
        String expectedValue = "testValue";
        when(valueOperations.get(key)).thenReturn(expectedValue);

        Object result = redisService.getValue(key);

        assertEquals(expectedValue, result);
        verify(valueOperations).get(key);
    }

    @Test
    void testDeleteKey() {
        String key = "testKey";
        when(redisTemplate.delete(key)).thenReturn(true);

        Boolean result = redisService.deleteKey(key);

        assertTrue(result);
        verify(redisTemplate).delete(key);
    }

    @Test
    void testHasKey() {
        String key = "testKey";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        Boolean result = redisService.hasKey(key);

        assertTrue(result);
        verify(redisTemplate).hasKey(key);
    }

    @Test
    void testExpire() {
        String key = "testKey";
        long timeout = 60L;
        TimeUnit unit = TimeUnit.SECONDS;
        when(redisTemplate.expire(key, timeout, unit)).thenReturn(true);

        Boolean result = redisService.expire(key, timeout, unit);

        assertTrue(result);
        verify(redisTemplate).expire(key, timeout, unit);
    }

    @Test
    void testGetExpire() {
        String key = "testKey";
        Long expectedTtl = 3600L;
        when(redisTemplate.getExpire(key)).thenReturn(expectedTtl);

        Long result = redisService.getExpire(key);

        assertEquals(expectedTtl, result);
        verify(redisTemplate).getExpire(key);
    }

    @Test
    void testSetHashValue() {
        String key = "hashKey";
        String hashKey = "field1";
        String value = "value1";

        redisService.setHashValue(key, hashKey, value);

        verify(hashOperations).put(key, hashKey, value);
    }

    @Test
    void testGetHashValue() {
        String key = "hashKey";
        String hashKey = "field1";
        String expectedValue = "value1";
        when(hashOperations.get(key, hashKey)).thenReturn(expectedValue);

        Object result = redisService.getHashValue(key, hashKey);

        assertEquals(expectedValue, result);
        verify(hashOperations).get(key, hashKey);
    }

    @Test
    void testGetAllHashEntries() {
        String key = "hashKey";
        Map<Object, Object> expectedMap = new HashMap<>();
        expectedMap.put("field1", "value1");
        expectedMap.put("field2", "value2");
        when(hashOperations.entries(key)).thenReturn(expectedMap);

        Map<Object, Object> result = redisService.getAllHashEntries(key);

        assertEquals(expectedMap, result);
        verify(hashOperations).entries(key);
    }

    @Test
    void testDeleteHashKey() {
        String key = "hashKey";
        Object[] hashKeys = {"field1", "field2"};
        when(hashOperations.delete(key, hashKeys)).thenReturn(2L);

        Long result = redisService.deleteHashKey(key, hashKeys);

        assertEquals(2L, result);
        verify(hashOperations).delete(key, hashKeys);
    }

    @Test
    void testRightPush() {
        String key = "listKey";
        String value = "value1";
        when(listOperations.rightPush(key, value)).thenReturn(1L);

        Long result = redisService.rightPush(key, value);

        assertEquals(1L, result);
        verify(listOperations).rightPush(key, value);
    }

    @Test
    void testLeftPush() {
        String key = "listKey";
        String value = "value1";
        when(listOperations.leftPush(key, value)).thenReturn(1L);

        Long result = redisService.leftPush(key, value);

        assertEquals(1L, result);
        verify(listOperations).leftPush(key, value);
    }

    @Test
    void testGetListRange() {
        String key = "listKey";
        long start = 0L;
        long end = 10L;
        List<Object> expectedList = Arrays.asList("value1", "value2", "value3");
        when(listOperations.range(key, start, end)).thenReturn(expectedList);

        List<Object> result = redisService.getListRange(key, start, end);

        assertEquals(expectedList, result);
        verify(listOperations).range(key, start, end);
    }

    @Test
    void testGetListSize() {
        String key = "listKey";
        when(listOperations.size(key)).thenReturn(5L);

        Long result = redisService.getListSize(key);

        assertEquals(5L, result);
        verify(listOperations).size(key);
    }

    @Test
    void testAddToSet() {
        String key = "setKey";
        Object[] values = {"value1", "value2"};
        when(setOperations.add(key, values)).thenReturn(2L);

        Long result = redisService.addToSet(key, values);

        assertEquals(2L, result);
        verify(setOperations).add(key, values);
    }

    @Test
    void testGetSetMembers() {
        String key = "setKey";
        Set<Object> expectedSet = new HashSet<>(Arrays.asList("value1", "value2"));
        when(setOperations.members(key)).thenReturn(expectedSet);

        Set<Object> result = redisService.getSetMembers(key);

        assertEquals(expectedSet, result);
        verify(setOperations).members(key);
    }

    @Test
    void testIsMemberOfSet() {
        String key = "setKey";
        String value = "value1";
        when(setOperations.isMember(key, value)).thenReturn(true);

        Boolean result = redisService.isMemberOfSet(key, value);

        assertTrue(result);
        verify(setOperations).isMember(key, value);
    }

    @Test
    void testAddToSortedSet() {
        String key = "zsetKey";
        String value = "value1";
        double score = 10.5;
        when(zSetOperations.add(key, value, score)).thenReturn(true);

        Boolean result = redisService.addToSortedSet(key, value, score);

        assertTrue(result);
        verify(zSetOperations).add(key, value, score);
    }

    @Test
    void testGetSortedSetByScore() {
        String key = "zsetKey";
        double min = 0.0;
        double max = 100.0;
        Set<Object> expectedSet = new LinkedHashSet<>(Arrays.asList("value1", "value2"));
        when(zSetOperations.rangeByScore(key, min, max)).thenReturn(expectedSet);

        Set<Object> result = redisService.getSortedSetByScore(key, min, max);

        assertEquals(expectedSet, result);
        verify(zSetOperations).rangeByScore(key, min, max);
    }

    @Test
    void testGetSortedSetRange() {
        String key = "zsetKey";
        long start = 0L;
        long end = 10L;
        Set<Object> expectedSet = new LinkedHashSet<>(Arrays.asList("value3", "value2", "value1"));
        when(zSetOperations.reverseRange(key, start, end)).thenReturn(expectedSet);

        Set<Object> result = redisService.getSortedSetRange(key, start, end);

        assertEquals(expectedSet, result);
        verify(zSetOperations).reverseRange(key, start, end);
    }

    @Test
    void testGetSortedSetScore() {
        String key = "zsetKey";
        String member = "value1";
        Double expectedScore = 25.5;
        when(zSetOperations.score(key, member)).thenReturn(expectedScore);

        Double result = redisService.getSortedSetScore(key, member);

        assertEquals(expectedScore, result);
        verify(zSetOperations).score(key, member);
    }

    @Test
    void testRemoveSortedSetMembers() {
        String key = "zsetKey";
        Object[] members = {"value1", "value2"};
        when(zSetOperations.remove(key, members)).thenReturn(2L);

        Long result = redisService.removeSortedSetMembers(key, members);

        assertEquals(2L, result);
        verify(zSetOperations).remove(key, members);
    }


    @Test
    void testMaintainFeedKeyLRUWithException() {
        String feedKey = "feed:123";
        int maxKeys = 500;
        long ttlSeconds = 60000L;
        String lruListKey = "feed_keys_lru";

        when(listOperations.leftPush(lruListKey, feedKey)).thenThrow(new RuntimeException("Redis error"));

        redisService.maintainFeedKeyLRU(feedKey, maxKeys, ttlSeconds);

        verify(listOperations).leftPush(lruListKey, feedKey);
    }

    @Test
    void testIncrement() {
        String key = "counterKey";
        long delta = 5L;
        when(valueOperations.increment(key, delta)).thenReturn(10L);

        Long result = redisService.increment(key, delta);

        assertEquals(10L, result);
        verify(valueOperations).increment(key, delta);
    }

    @Test
    void testDecrement() {
        String key = "counterKey";
        long delta = 3L;
        when(valueOperations.decrement(key, delta)).thenReturn(7L);

        Long result = redisService.decrement(key, delta);

        assertEquals(7L, result);
        verify(valueOperations).decrement(key, delta);
    }

    @Test
    void testGetValueReturnsNull() {
        String key = "nonExistentKey";
        when(valueOperations.get(key)).thenReturn(null);

        Object result = redisService.getValue(key);

        assertNull(result);
        verify(valueOperations).get(key);
    }

    @Test
    void testDeleteKeyReturnsFalse() {
        String key = "nonExistentKey";
        when(redisTemplate.delete(key)).thenReturn(false);

        Boolean result = redisService.deleteKey(key);

        assertFalse(result);
        verify(redisTemplate).delete(key);
    }

    @Test
    void testHasKeyReturnsFalse() {
        String key = "nonExistentKey";
        when(redisTemplate.hasKey(key)).thenReturn(false);

        Boolean result = redisService.hasKey(key);

        assertFalse(result);
        verify(redisTemplate).hasKey(key);
    }
}