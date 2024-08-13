package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.GlobalUtil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:00
 **/
public class ThreadNameFactory implements ThreadFactory {

    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private final String prefix;

    public ThreadNameFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override public Thread newThread(Runnable r) {
        final String name = prefix + "-" + atomicInteger.getAndIncrement();
        return new Thread(
                () -> {
                    while (true) {
                        try {
                            r.run();
                            break;
                        } catch (Throwable throwable) {
                            if (throwable instanceof InterruptedException) {break;}
                            GlobalUtil.exception("线程【" + name + "】异常", throwable);
                        }
                    }
                },
                name);
    }

}
