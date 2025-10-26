package com.smooth.sse.service;

import com.smooth.sse.dto.req.SendMessageParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 10:52
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class MsgService {

    private final SseService sseService;

    public void sendMsg(SendMessageParams req) {

        //业务逻辑：会话、消息入库

        //发送消息
        sseService.sendMsg(req.getReceiverId(), req.getContent());
    }
}
