package com.smooth.mq.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.Serial;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-06 17:09
 **/
@Service
@Slf4j
@RocketMQTransactionListener
public class TransactionMsgConsumer implements RocketMQLocalTransactionListener {

    /**
     * 执行本地事务
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("<<< 执行本地事务：{}, arg:{}", msg.getPayload(), arg.toString());
        try {

            //业务逻辑

            // 本地事务成功
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            // 本地事务失败
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事务
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        log.info("<<< 检查本地事务：{}", msg.getPayload());
        return RocketMQLocalTransactionState.COMMIT;
    }
}
