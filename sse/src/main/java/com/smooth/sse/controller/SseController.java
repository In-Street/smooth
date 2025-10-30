package com.smooth.sse.controller;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 10:26
 **/

import com.smooth.sse.dto.req.SendMessageParams;
import com.smooth.sse.service.MsgService;
import com.smooth.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final MsgService msgService;

    @GetMapping("/createConnection/{clientId}")
    public SseEmitter createConnection(@PathVariable Long clientId){
        return sseService.createConnection(clientId);
    }
    
    
    @PostMapping("/sendMsg")
    public void sendMsg(@RequestBody SendMessageParams req){
        msgService.sendMsg(req);
    }
}
