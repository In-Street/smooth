package com.smooth.redis.controller;

import com.smooth.redis.dto.SendMsgReq;
import com.smooth.redis.dto.User;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import com.smooth.redis.service.PubSubService;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 10:23
 **/
@RestController
@RequestMapping("/pubSub")
public class PubSubController {
    
    @Resource
    private PubSubService pubSubService;
    
    @GetMapping("/saveData")
    public void saveData(){
        pubSubService.saveData();
    }
    
    @GetMapping("/getData")
    public User getData(String key){
        return pubSubService.getData(key);
    }

    @PostMapping("/sendMsg")
    public void sendMsg(@RequestBody SendMsgReq req){
        pubSubService.sendMessage(req.getTopic(),req.getMsg());
    }

    @GetMapping("/schedulePoolTest")
    public void schedulePoolTest(){
        pubSubService.schedulePoolTest();
    }

}
