package com.smooth.redis.listeners;

import com.alibaba.fastjson2.JSONObject;
import com.smooth.redis.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 11:06
 **/
@Component
@Slf4j
public class MsgListener implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {

        byte[] channel = message.getChannel();
        byte[] body = message.getBody();

        String channelString = new String(channel);
        String bodyString = new String(body);

        log.info("channelString: {} , bodyString:{}", channelString, bodyString);

        User user = JSONObject.parseObject(bodyString, User.class);
        log.info("user: {}", user);

    }
}
