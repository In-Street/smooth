package com.smooth.sse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 10:03
 **/
@Configuration
public class PoolConfig {

    /**
     *  定时续约线程池
     * @return
     */
    @Bean(name="selfScheduledThreadPool")
    public ScheduledThreadPoolExecutor scheduledThreadPoolExecutor(){
        ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(
                5,
                new ThreadFactory() {
                    AtomicInteger atomicInteger = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        System.out.println("=== 创建新定时线程 ===");
                        Thread thread = new Thread(r, "renew-thread-" + atomicInteger.getAndIncrement());
                        thread.setDaemon(true);
                        return thread;
                    }
                }
        );
        return poolExecutor;
    }
}
