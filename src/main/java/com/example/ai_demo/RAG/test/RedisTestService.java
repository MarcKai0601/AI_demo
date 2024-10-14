package com.example.ai_demo.RAG.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String testConnection() {
        try {
            // 写入一个键值对到 Redis
            redisTemplate.opsForValue().set("testKey", "Hello Redis");
            // 读取 Redis 中的值
            return redisTemplate.opsForValue().get("testKey");
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection failed";
        }
    }
}
