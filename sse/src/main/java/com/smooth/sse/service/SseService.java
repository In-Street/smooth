package com.smooth.sse.service;

import com.smooth.sse.config.RedisConfig;
import com.smooth.sse.dto.req.SendMessageParams;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 10:26
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SseService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ScheduledThreadPoolExecutor selfScheduledThreadPool;
    private final RedisTemplate<String, Object> redisTemplate;

    private ConcurrentHashMap<Long, SseEmitter> localEmitterMap = new ConcurrentHashMap<>();

    // 当前节点名称
    private final String NODE_ID = "node-1";

    // Redis 前缀
    private final String NODE_KEY = "sse:node:";

    // 应用退出，销毁线程池
    @PreDestroy
    public void destroy() {
        selfScheduledThreadPool.shutdown();
        try {
            if (!selfScheduledThreadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                selfScheduledThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            selfScheduledThreadPool.shutdownNow();
        }
    }

    public SseEmitter createConnection(Long clientId) {

        // 创建SseEmitter（设置30分钟超时）
        SseEmitter sseEmitter = new SseEmitter(30 * 1000L);

        // sseEmitter 存入本地
        localEmitterMap.put(clientId, sseEmitter);

        // 建立 client_id 与 node_id 的关联关系
        stringRedisTemplate.opsForValue().set(NODE_KEY + clientId, NODE_ID, 30, TimeUnit.SECONDS);

        // 开启关联关系续约
        ScheduledFuture<?> renewTask = selfScheduledThreadPool.scheduleAtFixedRate(() -> {
            if (localEmitterMap.containsKey(clientId)) {
                log.info(">> 续约客户端 clientId:{} , nodeId:{} '", clientId, NODE_ID);
                stringRedisTemplate.expire(NODE_KEY + clientId, 30, TimeUnit.SECONDS);
            }
        }, 0, 5, TimeUnit.SECONDS);

        sseEmitter.onTimeout(() -> {
            log.info("SSE Emitter timeout , clientId {}", clientId);
            cleanup(clientId, renewTask);
        });

        sseEmitter.onCompletion(() -> {
            log.info("SSE Emitter complete , clientId {}", clientId);
            cleanup(clientId, renewTask);
        });

        sseEmitter.onError(t -> {
            log.error("SSE Emitter error , clientId  " + clientId, t);
            cleanup(clientId, renewTask);
        });

        // 发送连接建立成功消息
        try {
            sseEmitter.send(SseEmitter.event().name("Connection").data("成功建立连接..."));
        } catch (IOException e) {
            log.error("SSE Emitter 发送消息失败 , clientId  " + clientId, e);
            sseEmitter.completeWithError(e);
            cleanup(clientId, renewTask);
        }
        return sseEmitter;
    }

    public void sendMsgBasic(Long clientId, String msg) {
        SseEmitter sseEmitter = localEmitterMap.get(clientId);
        if (Objects.isNull(sseEmitter)) {
            return;
        }
        try {
            sseEmitter.send(SseEmitter.event().name("Message").data(msg));
        } catch (IOException e) {
            log.error("SSE Emitter 发送消息失败 , clientId  " + clientId, e);
            sseEmitter.completeWithError(e);
            cleanup(clientId, null);
        }
    }

    public void sendMsg(String msg, Long clientId) {

        String nodeId = stringRedisTemplate.opsForValue().get(NODE_KEY + clientId);
        if (NODE_ID.equals(nodeId)) {
            sendMsgBasic(clientId, msg);
            return;
        }
        SendMessageParams sendMessageParams = new SendMessageParams();
        sendMessageParams.setReceiverId(clientId);
        sendMessageParams.setContent(msg);
        redisTemplate.convertAndSend(RedisConfig.channelName, sendMessageParams);
    }

    private void cleanup(Long clientId, ScheduledFuture renewTask) {

        if (Objects.nonNull(renewTask) && !renewTask.isDone()) {
            // true: 执行任务的线程被中断  false：温和的取消，允许当前任务执行完
            renewTask.cancel(false);
        }
        SseEmitter sseEmitter = localEmitterMap.get(clientId);
        if (Objects.nonNull(sseEmitter)) {
            sseEmitter.complete();
        }
        localEmitterMap.remove(clientId);
        // 删除映射关系
        stringRedisTemplate.delete(NODE_KEY + clientId);
    }
}
