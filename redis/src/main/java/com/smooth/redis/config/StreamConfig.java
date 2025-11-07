package com.smooth.redis.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-31 11:28
 **/
@Component
public class StreamConfig {

    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    //stream 名称、消费者组名称
    public static final String stream_key = "stream:self:";
    public static final String group_name = "group:consumer:";
    public static final String group_b_name = "group:b:consumer:";

    // 每次拉取消息的最大数量
    public static final int stream_batch_size = 10;

    // Stream最大消息数（超过则删除 oldest 消息，避免内存膨胀）
    public static final long stream_max_length = 10000;

    /**
     * 初始化 stream 和 消费者组
     *      1. 一个 Stream 下可以创建多个 Consumer Group（消费者组）。同一个消息可以被多个消费者组消费（即广播给多个消费者组）。在需要将同一消息发送给多个不同业务场景时非常有用。
     *      2. 在一个消费者组内，可以包含多个消费者（Consumer），组内的消费者共同消费同一个 Stream 的消息，每条消息只会被组内的一个消费者消费（即竞争关系）
     *      3. 消息确认机制：当消息被消费者处理完成后，消费者会向 Redis 发送一个确认命令（XACK），这样这条消息就会被标记为已处理，并从消费者的待处理消息列表（Pending List）中移除。未 ACK 的消息会进入 pending 列表，可重试
     */

    /**
     *   假设有一个订单处理的 Stream，名为 order:stream，我们可以创建两个消费者组：
     *      order:group:payment：负责处理支付相关的消息。
     *      order:group:notification：负责发送通知消息。
     *      当一条新的订单消息被发送到 order:stream时，这两个消费者组都会收到这条消息，然后由各自组内的消费者进行消费
     */

    /**
     *   问题：
     *          1. 不同消费者组的消费偏移量 如何指定
     *          2. 如何从指定位置开始消费
     */
    @PostConstruct
    public void init() {

        // stream 是否存在
        Boolean existStream = objectRedisTemplate.hasKey(stream_key);
        initGroupA();
        initGroupB();
    }

    private void initGroupA() {
        // group 是否存在
        boolean existGroup = objectRedisTemplate.opsForStream().groups(stream_key).stream().anyMatch(s -> s.groupName().equals(group_name));
        if (existGroup) {
            return;
        }

        objectRedisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //  XGROUP CREATE [stream] [group] $ MKSTREAM：$表示从最新消息开始消费，MKSTREAM不存在则创建Stream
                connection.streamCommands()
                        .xGroupCreate(stream_key.getBytes(StandardCharsets.UTF_8), group_name, ReadOffset.latest(), true);
                return null;
            }
        });
    }

    private void initGroupB() {
        // group 是否存在
        boolean existGroup = objectRedisTemplate.opsForStream().groups(stream_key).stream().anyMatch(s -> s.groupName().equals(group_b_name));
        if (existGroup) {
            return;
        }

        objectRedisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //  XGROUP CREATE [stream] [group] $ MKSTREAM：$表示从最新消息开始消费，MKSTREAM不存在则创建Stream
                connection.streamCommands()
                        .xGroupCreate(stream_key.getBytes(StandardCharsets.UTF_8), group_b_name, ReadOffset.latest(), true);
                return null;
            }
        });
    }
}
