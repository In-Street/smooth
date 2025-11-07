package com.smooth.redis.config;

import com.smooth.redis.listeners.pubsub.MainMsgListener;
import com.smooth.redis.listeners.pubsub.MsgListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 09:19
 **/
@Configuration
public class RedisPubSubConfig {

    public static final String CHANNEL_TOPIC = "channelTopic";
    public static final String MAIN_CHANNEL_TOPIC = "main_channel_topic";


    // ####################################    定义多个频道    ####################################
    @Bean(name = "channelTopic")
    public ChannelTopic channelTopic() {
        return new ChannelTopic(CHANNEL_TOPIC);
    }

    @Bean(name = "mainChannelTopic")
    public ChannelTopic mainChannelTopic() {
        return new ChannelTopic(MAIN_CHANNEL_TOPIC);
    }


    // ####################################    创建多个监听器，用于处理不同频道的消息    ####################################
    @Bean(name = "messageListener")
    public MessageListenerAdapter messageListener(MsgListener listener) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(listener);
        return messageListenerAdapter;
    }

    @Bean(name = "messageListenerForMain")
    public MessageListenerAdapter messageListenerForMain(MainMsgListener listener) {
        return new MessageListenerAdapter(listener);
    }


    @Bean(name = "redisMessageListenerContainer")
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory,
                                                                       MessageListenerAdapter messageListener,
                                                                       MessageListenerAdapter messageListenerForMain) {

        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);

        // 将频道和监听器绑定
        redisMessageListenerContainer.addMessageListener(messageListener, channelTopic());
        redisMessageListenerContainer.addMessageListener(messageListenerForMain, mainChannelTopic());

        return redisMessageListenerContainer;
    }
}
