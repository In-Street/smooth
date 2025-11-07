package com.smooth.redis.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smooth.redis.config.StreamConfig;
import com.smooth.redis.dto.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-31 11:54
 **/
@Service
@Slf4j
public class StreamService {

    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    public RecordId produce(String streamKey ,User user) {

        System.out.println(objectRedisTemplate.getValueSerializer().getClass().getName());

        RecordId recordId = objectRedisTemplate.opsForStream()
                .add(StreamRecords.newRecord()
                        // .ofObject(user) //此种方式存入后，id、username 、class 内容都是 base64的
                        .ofObject(JSON.toJSONString(user))
                        .withStreamKey(streamKey)
                );

        return recordId;
    }

    /**
     * 读取但不消费，消息进入pending list 中
     *
     * @return
     */
    public List<User> consume(String groupName,String consumerName) {
        /**
         *  ReadOffset.latest: 表示 “从当前 Stream 中最新的消息开始读取”（即只读取后续新增的消息，不回溯历史消息）。
         *  使用 XREADGROUP消费者组时，消费组不需要 “从最新消息开始”，而是需要 “从未处理的消息开始”，因此 Redis 会拒绝 ReadOffset.latest 作为 XREADGROUP 的偏移量，抛出错误：ERR The $ ID is meaningless in the context of XREADGROUP
         *
         *  消费者组对偏移量的要求：
         *      1. 读取当前消费组中从未被任何消费者处理过的消息。
         *      2. 具体的消息ID： 表示 “从该 ID 之后的消息开始读取”
         *
         */
        List<MapRecord<String, Object, Object>> readResult = objectRedisTemplate.opsForStream().read(
                Consumer.from(groupName, consumerName),
                StreamReadOptions.empty().count(1).block(Duration.of(1L, ChronoUnit.SECONDS)),
                StreamOffset.create(StreamConfig.stream_key, ReadOffset.lastConsumed())
        );

        ArrayList<RecordId> recordIdList = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for (MapRecord<String, Object, Object> record : readResult) {
            User user = JSONObject.parseObject(record.getValue().get("payload").toString(), User.class);
            users.add(user);
            RecordId recordId = record.getId();
            recordIdList.add(recordId);
        }

        // 消息确认
        // Long acknowledgeResult = redisTemplate.opsForStream().acknowledge(StreamConfig.stream_key, StreamConfig.group_name, recordIdList.toArray(new RecordId[0]));

        // redisTemplate.opsForStream().pending()
        return users;
    }

    /**
     *  从 pending list中获取消息，重新消费，ack确认
     */
    public void getPendingList(String groupName,String consumerName) {

        // 获取各消费者组的消费进度
        StreamInfo.XInfoGroups groups = objectRedisTemplate.opsForStream().groups(StreamConfig.stream_key);
        for (StreamInfo.XInfoGroup group : groups) {
            String groupedName = group.groupName();
            Long consumerCount = group.consumerCount();
            Long pendingCount = group.pendingCount();
            String lastDeliveredId = group.lastDeliveredId();
        }

        PendingMessages pending = objectRedisTemplate.opsForStream().pending(StreamConfig.stream_key, groupName, Range.unbounded(), 100L);
        long totalPendingMessages = pending.get().count();
        log.info("总共未 ACK的数据有: {} 条 ，所属消费组：{}", totalPendingMessages,pending.getGroupName());

        for (PendingMessage message : pending) {
            String msgId = message.getIdAsString();
            log.info("msgId: {}，所属消费者：{}", msgId,message.getConsumerName());

            // 指定消息id 获取
            List<MapRecord<String, Object, Object>> records = objectRedisTemplate.opsForStream().read(
                    Consumer.from(groupName, message.getConsumerName()),
                    StreamReadOptions.empty().count(10).block(Duration.of(1L, ChronoUnit.SECONDS)),
                    StreamOffset.create(StreamConfig.stream_key, ReadOffset.from(message.getId()))
            );


            if (CollectionUtils.isEmpty(records)) {
                // 一个stream下有多个消费者组（group-a、group-b），各自对应各自的消费者（consumer-a、consumer-b）,两个消费者组都read了消息但未ack，各自的pending list中存在此条消息。从pending list获取到消息id 使用read方法是读取不到的，使用claim来解决【明确声明消息所有权的转移、提供原子性的消息认领操作】。
                //  当同一条消息在多个消费者组中都处于pending状态时，会产生消息所有权的模糊性，导致read获取不到数据。这是Redis Stream为了保证消息处理的确定性和避免竞态条件而采取的设计
                records =
                        objectRedisTemplate.opsForStream().claim(
                                StreamConfig.stream_key,
                                groupName,
                                consumerName,
                                Duration.ofMinutes(1), // 最小空闲时间
                                message.getId()
                        );
                if (CollectionUtils.isEmpty(records)) {
                    continue;
                }

            }
            MapRecord<String, Object, Object> record = records.getFirst();
            User user = JSONObject.parseObject(record.getValue().get("payload").toString(), User.class);
            RecordId recordId = record.getId();
            log.info("从Pending List 中消费消息，msgId: {} , user: {}", recordId, user);

            objectRedisTemplate.opsForStream().acknowledge(StreamConfig.stream_key, groupName, msgId);
        }
        System.out.println();

    }


    public void getStreamInfo(){
        // 获取各消费者组的消费进度
        StreamInfo.XInfoGroups groups = objectRedisTemplate.opsForStream().groups(StreamConfig.stream_key);
        for (StreamInfo.XInfoGroup group : groups) {
            String groupedName = group.groupName();
            Long consumerCount = group.consumerCount();
            Long pendingCount = group.pendingCount();
            String lastDeliveredId = group.lastDeliveredId();
        }
    }
}
