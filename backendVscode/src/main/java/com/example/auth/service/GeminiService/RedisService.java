package com.example.auth.service.GeminiService;
import com.example.auth.dto.JobDTO.JobResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class RedisService {
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    public RedisService() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        this.jedisPool = new JedisPool(poolConfig, "localhost", 6379);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    public String saveRecommendedJobs (Long profileId , List<JobResponse> recommendedJobs){
        String key = "pro"+profileId;
        String res="";
        try (Jedis jedis = jedisPool.getResource()) {
            String json = objectMapper.writeValueAsString(recommendedJobs);
            res = jedis.set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize jobs list", e);
        }
        return res ;
    }
    public List<JobResponse> getRecommendedJobs (Long profileId ){
        String key = "pro"+profileId.toString();
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, JobResponse.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize jobs list", e);
        }
    }
    public void clearRecommendedJobs(Long profileId) {
        String key = "pro" + profileId ;
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

}
