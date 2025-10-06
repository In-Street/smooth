package com.smooth.mq.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 消息实体
 * @author Cheng Yufei
 * @create 2025-10-06 16:36
 **/
@Data
public class OrderMessage implements Serializable {

    private static final long serialVersionUID = 5482586808319148464L;

    private Long orderId;
    private String productName;
    private Double amount;
}
