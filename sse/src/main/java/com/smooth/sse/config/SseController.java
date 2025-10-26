package com.smooth.sse.config;

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


@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;
    private final MsgService msgService;

    @GetMapping("/createConnection.{clientId}")
    public void createConnection(@PathVariable Long clientId){
        sseService.createConnection(clientId);
    }
    
    
    @PostMapping("/sendMsg")
    public void sendMsg(@RequestBody SendMessageParams req){
        msgService.sendMsg(req);
    }
}
