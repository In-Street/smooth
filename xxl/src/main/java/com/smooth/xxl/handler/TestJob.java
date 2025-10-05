package com.smooth.xxl.handler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 *
 * @author Cheng Yufei
 * @create 2025-10-05 17:24
 **/
@Component
public class TestJob {

    @XxlJob(value = "selfSimpleJobHandle")
    public void test() throws InterruptedException {
        XxlJobHelper.log("Hello World");
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            Thread.sleep(2000);
        }
    }

}
