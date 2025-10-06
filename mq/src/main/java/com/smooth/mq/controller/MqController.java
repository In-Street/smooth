package com.smooth.mq.controller;

import com.smooth.mq.model.OrderMessage;
import com.smooth.mq.producer.ProducerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-06 17:14
 **/
@RestController
@RequestMapping("/mq")
public class MqController {

    @Resource
    private ProducerService producerService;

    @GetMapping("/send-simple")
    public String sendSimpleMessage() {
        producerService.sendNormalMsg("TEST_TOPIC", "Hello RocketMQ!");
        return "Normal message sent!";
    }

    @GetMapping("/send-tag")
    public String sendTagMessage() {
        producerService.sendMsgWithTag("TEST_TOPIC", "TAG_ANONY", "Message with TAG_A");
        return "Tag message sent!";
    }

    @GetMapping("/send-order")
    public String sendOrderMessage() {
        OrderMessage order = new OrderMessage();
        order.setOrderId(1001L);
        order.setProductName("MacBook Pro");
        order.setAmount(1999.99);

        producerService.sendObjectMsg("ORDER_MESSAGE_TOPIC", order);
        return "Order message sent!";
    }

    @GetMapping("/send-sequence")
    public String sendOrderedMessage() {
        for (int i = 0; i < 10; i++) {
            producerService.sendSequenceMsg("SEQUENCE_TOPIC", "Ordered message " + i, i == 8 ? 1001L : 1000L);
        }
        return "Sequence messages sent!";
    }

    @GetMapping("/send-transaction")
    public String sendTransactionMessage() {
        producerService.sendTransactionMsg("tx-topic", "Transaction message",2000L);
        return "Transaction message sent!";
    }
}
