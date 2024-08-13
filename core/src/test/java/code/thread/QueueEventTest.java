package code.thread;

import org.junit.Test;
import wxdgaming.spring.boot.core.threading.QueueEvent;

import java.util.concurrent.TimeUnit;

/**
 * 队列测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 16:21
 **/
public class QueueEventTest {

    @Test
    public void t1() throws InterruptedException {
        QueueEvent queueEvent = new QueueEvent("test");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            final int index = i + 1;
            queueEvent.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " - " + index + " -" + start + " -" + System.currentTimeMillis());
            });
        }
        queueEvent.getExecutor().scheduleWithFixedDelay(() -> {
            System.out.println(Thread.currentThread().getName() + " - schedule -" + start + " -" + System.currentTimeMillis());
        }, 1, 1, TimeUnit.SECONDS);
        Thread.sleep(6000);
        queueEvent.getExecutor().shutdown();
    }

}
