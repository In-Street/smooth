package com.smooth.redis.service;

import com.smooth.redis.dto.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 10:15
 **/
@Service
@Slf4j
public class PubSubService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ChannelTopic channelTopic;


    public void saveData() {
        String key = "key3";
        User user = new User();
        user.setId(22).setUsername("一首歌的时间");

        redisTemplate.opsForValue().set(key, user);

    }

    public User getData(String key) {
        User user = (User) redisTemplate.opsForValue().get(key);
        System.out.println(user);
        return user;
    }

    public void sendMessage(String topic,User msg) {

        redisTemplate.convertAndSend(topic, msg);
    }


}
