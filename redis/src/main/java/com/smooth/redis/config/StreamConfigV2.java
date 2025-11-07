package com.smooth.redis.config;

import com.smooth.redis.listeners.stream.StreamMsgListener;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

/**
 *
 * @author Cheng Yufei
 * @create 2025-11-07 11:58
 **/
@Configuration
public class StreamConfigV2 {

    @Resource
    private RedisTemplate objectRedisTemplate;

    public static final String stream_key = "stream-v2";
    public static final String group_a = "group-a";
    public static final String consumer_a = "consumer-a";

    public static final String group_b = "group-b";
    public static final String consumer_b = "consumer-b";


    @Bean(name = "streamMessageListenerContainer")
    public StreamMessageListenerContainer streamMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, StreamMsgListener streamMsgListener) {

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory,
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(1)
                        .build()
        );

        if (objectRedisTemplate.opsForStream().groups(stream_key).stream().noneMatch(s -> s.groupName().equals(group_a))) {
            objectRedisTemplate.opsForStream().createGroup(stream_key, group_a);
        }

        if (objectRedisTemplate.opsForStream().groups(stream_key).stream().noneMatch(s -> s.groupName().equals(group_b))) {
            objectRedisTemplate.opsForStream().createGroup(stream_key, group_b);
        }

        listenerContainer.start();

        listenerContainer.register(StreamMessageListenerContainer.StreamReadRequest.builder(StreamOffset.create(stream_key, ReadOffset.lastConsumed()))
                        .consumer(Consumer.from(group_a, consumer_a))
                        .autoAcknowledge(true)
                        .cancelOnError(throwable -> false)
                        .build()
                , streamMsgListener
        );

        listenerContainer.register(StreamMessageListenerContainer.StreamReadRequest.builder(StreamOffset.create(stream_key, ReadOffset.lastConsumed()))
                        .consumer(Consumer.from(group_b, consumer_b))
                        .autoAcknowledge(true)
                        .build()
                , streamMsgListener
        );

        return listenerContainer;
    }

}
