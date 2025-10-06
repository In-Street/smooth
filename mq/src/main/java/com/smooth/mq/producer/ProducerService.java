package com.smooth.mq.producer;

import com.smooth.mq.model.OrderMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-06 16:38
 **/
@Service
@Slf4j
public class ProducerService {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送普通消息
     */
    public void sendNormalMsg(String topic, String msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
    }

    /**
     * 发送带标签的消息
     */
    public void sendMsgWithTag(String topic, String tag, String msg) {
        rocketMQTemplate.send(topic + ":" + tag, MessageBuilder.withPayload(msg).build());
    }

    /**
     * 发送对象消息
     */
    public void sendObjectMsg(String topic, OrderMessage msg) {
        rocketMQTemplate.convertAndSend(topic, msg);
    }

    /**
     * 发送异步消息
     */
    public void sendAsyncMsg(String topic, String msg) {
        rocketMQTemplate.asyncSend(topic, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info(">>>异步消息发送成功：{}", sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error(">>>>异步消息发送失败，", throwable);
            }
        });
    }

    /**
     *  发送顺序消息
     * @param orderId ：用于选择队列
     */
    public void sendSequenceMsg(String topic, String msg, Long orderId) {
        rocketMQTemplate.syncSendOrderly(topic, msg, orderId.toString());
    }

    /**
     *  发送事务消息
     */
    public void sendTransactionMsg(String topic, String msg, Long orderId) {
        rocketMQTemplate.sendMessageInTransaction(
                topic,
                MessageBuilder.withPayload(msg).build(),
                orderId //参数
        );
    }
}
