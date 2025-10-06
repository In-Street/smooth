package com.smooth.mq.consumer;

import com.smooth.mq.model.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 对象消息处理
 * @author Cheng Yufei
 * @create 2025-10-06 16:55
 **/
@Service
@Slf4j
@RocketMQMessageListener(
        topic = "ORDER_MESSAGE_TOPIC",
        consumerGroup = "${rocketmq.consumer.group}"
)
public class OrderMessageConsumer implements RocketMQListener<OrderMessage> {
    @Override
    public void onMessage(OrderMessage orderMessage) {
        log.info("<<<< 处理订单消息：{}", orderMessage);
    }
}
