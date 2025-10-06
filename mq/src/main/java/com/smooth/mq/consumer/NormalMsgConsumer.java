package com.smooth.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 普通消息消费者
 * @author Cheng Yufei
 * @create 2025-10-06 16:47
 **/
@Service
@Slf4j
@RocketMQMessageListener(
        topic = "TEST_TOPIC",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class NormalMsgConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        log.info("<<<< 普通消息处理: {}", message);
        //业务逻辑
    }
}
