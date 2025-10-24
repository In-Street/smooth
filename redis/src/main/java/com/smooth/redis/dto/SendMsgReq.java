package com.smooth.redis.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 11:42
 **/
@Data
public class SendMsgReq implements Serializable {

    private static final long serialVersionUID = 813823735328375559L;

    private String topic;
    private User msg;
}
