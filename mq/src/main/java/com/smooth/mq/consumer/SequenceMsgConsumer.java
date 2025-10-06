package com.smooth.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 顺序消息消费者
 *
 * @author Cheng Yufei
 * @create 2025-10-06 16:57
 **/
@Service
@Slf4j
@RocketMQMessageListener(
        topic = "SEQUENCE_TOPIC",
        consumerGroup = "${rocketmq.consumer.group}",
        consumeMode = ConsumeMode.ORDERLY   // 设置为顺序消费
)
public class SequenceMsgConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        log.info("<<<<< 处理顺序消息:{}", s);
    }
}
