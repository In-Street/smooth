package com.smooth.sse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 10:23
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulePoolTest {

    private final  ScheduledThreadPoolExecutor selfScheduledThreadPool;

    public void schedulePoolTest(){
        FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            ScheduledFuture<?> scheduledFuture = selfScheduledThreadPool.scheduleAtFixedRate(() -> {
                System.out.println(Thread.currentThread().getName() + ">>> 我在执行任务 - "+ finalI + "   "+dateFormat.format(new Date()));
            }, 0, 5, TimeUnit.SECONDS);
        }
    }
}
