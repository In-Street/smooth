package com.smooth.mq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 *  标签消息消费者
 * @author Cheng Yufei
 * @create 2025-10-06 16:50
 **/
@Service
@Slf4j
@RocketMQMessageListener(
        topic = "TEST_TOPIC",
        consumerGroup = "${rocketmq.consumer.group}",
        selectorExpression = "TAG_ANONY"  // 只消费 TAG_ANONY 的消息
)
public class TagMegConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {
        log.info("<<<<< 处理带有标签：{} 的消息：{}", "TAG_ANONY", s);
    }
}
