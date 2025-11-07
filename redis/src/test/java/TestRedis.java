import com.alibaba.fastjson2.JSONObject;
import com.smooth.redis.dto.User;
import io.netty.handler.codec.base64.Base64Decoder;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.Test;

import java.util.Base64;
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

    @Test
    public void t2() {
        System.out.println(new String(Base64.getDecoder().decode("Y29tLnNtb290aC5yZWRpcy5kdG8uVXNlcg==")));
        System.out.println(new String(Base64.getDecoder().decode("c2hhbGxvdw==")));
        System.out.println(new String(Base64.getDecoder().decode("MTA=")));

        System.out.println(JSONObject.parseObject("{\"id\":2400,\"username\":\"taylor swift\"}", User.class));
    }
}
