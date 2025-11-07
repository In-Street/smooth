package com.smooth.redis.controller;

import com.smooth.redis.dto.User;
import com.smooth.redis.service.StreamService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-24 10:23
 **/
@RestController
@RequestMapping("/stream")
public class StreamController {

    @Resource
    private StreamService streamService;

    @PostMapping("/produce")
    public RecordId produce(@RequestBody User user,@RequestParam String streamKey) {
        return streamService.produce(streamKey,user);
    }

    @GetMapping("/consume")
    public List<User> consume(String groupName,String consumerName) {
        return streamService.consume(groupName,consumerName);
    }

    @GetMapping("/getPendingList")
    public void getPendingList(String groupName,String consumerName) {
        streamService.getPendingList(groupName,consumerName);
    }

}
