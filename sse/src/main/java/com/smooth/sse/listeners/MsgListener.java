package com.smooth.sse.listeners;

import com.alibaba.fastjson2.JSONObject;
import com.smooth.sse.dto.req.SendMessageParams;
import com.smooth.sse.service.SseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 16:33
 **/
@Component
@Slf4j
public class MsgListener implements MessageListener {

    @Resource
    private SseService sseService;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
        SendMessageParams params = JSONObject.parseObject(messageStr, SendMessageParams.class);
        log.debug("Pub/Sub 消息处理 message:{}", params);
        sseService.handlerMsg(params.getContent(), params.getReceiverId());
    }
}
