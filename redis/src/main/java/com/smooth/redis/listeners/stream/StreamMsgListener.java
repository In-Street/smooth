package com.smooth.redis.listeners.stream;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cheng Yufei
 * @create 2025-11-07 11:57
 **/
@Component
@Slf4j
public class StreamMsgListener implements StreamListener<String, MapRecord<String, String, String>> {

    @Resource
    private RedisTemplate objectRedisTemplate;

    @SneakyThrows
    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        String msgId = message.getId().getValue();
        String payload = message.getValue().get("payload");

        log.info("onMessage msgId:{}, payload:{}", msgId, payload);
        JSONObject jsonObject = JSONObject.parseObject(payload);
        String type = jsonObject.getString("type");
        switch (StringUtils.isBlank(type)?StringUtils.EMPTY:type) {
            case "common" -> {
                log.info("处理普通用户消息:{}", payload);
            }
            case "vip" -> {
                log.info("处理VIP消息:{}", payload);
            }
        }
        // objectRedisTemplate.opsForStream().acknowledge(StreamConfigV2.stream_key,message.get)
        System.out.println();
    }
}
