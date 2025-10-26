import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-26 09:16
 **/
public class TestRedis {

    @Test
    public void t1() {
        ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(
                5,
                new ThreadFactory() {
                    AtomicInteger atomicInteger = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "renew-thread-" + atomicInteger.getAndIncrement());
                        thread.setDaemon(true);
                        return thread;
                    }
                }
        );

        FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            ScheduledFuture<?> scheduledFuture = poolExecutor.scheduleAtFixedRate(() -> {
                System.out.println(Thread.currentThread().getName() + ">>> 我在执行任务 - "+ finalI + "   "+dateFormat.format(new Date()));
            }, 0, 5, TimeUnit.SECONDS);
        }

        System.out.println();

    }
}
