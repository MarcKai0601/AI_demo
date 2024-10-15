package com.example.ai_demo.RAG.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTestService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String testConnection() {
        try {
            // 写入一个键值对到 Redis
            redisTemplate.opsForValue().set("testKey", "Hello Redis",1, TimeUnit.MINUTES);
            // 读取 Redis 中的值
            return redisTemplate.opsForValue().get("testKey");
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection failed";
        }
    }

    public void testListOperations() {
        try {
            // 推入多个值到列表
            redisTemplate.opsForList().rightPush("testList", "value1");
            redisTemplate.opsForList().rightPush("testList", "value2");
            redisTemplate.opsForList().rightPush("testList", "value3");

            // 设置列表的过期时间为 1 分钟
            redisTemplate.expire("testList", 1, TimeUnit.MINUTES);

            // 获取整个列表
            System.out.println(redisTemplate.opsForList().range("testList", 0, -1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
