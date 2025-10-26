package com.smooth.sse.config;

import com.smooth.sse.listeners.MsgListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 16:30
 **/
@Configuration
public class RedisConfig {

    public static final String channelName = "sse-topic";

    @Bean(name = "redisTemplate")
    public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());

        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "channelTopic")
    public ChannelTopic channelTopic() {
        return new ChannelTopic(channelName);
    }

    @Bean(name = "sseMessageListener")
    public MessageListener messageListener(MsgListener msgListener) {
        return new MessageListenerAdapter(msgListener);
    }

    @Bean(name = "messageListenerContainer")
    public RedisMessageListenerContainer messageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            MessageListener sseMessageListener
    ) {
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(redisConnectionFactory);
        listenerContainer.addMessageListener(sseMessageListener, channelTopic());
        return listenerContainer;
    }
}
