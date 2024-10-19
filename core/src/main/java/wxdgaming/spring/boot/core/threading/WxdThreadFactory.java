package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.GlobalUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:00
 **/
public class WxdThreadFactory implements ThreadFactory {

    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private String prefix;

    /** 线程名字前缀 */
    public WxdThreadFactory(String prefix) {
        this.prefix = prefix;
        if (StringsUtil.notEmptyOrNull(this.prefix) && !this.prefix.endsWith("-")) {
            this.prefix += "-";
        }
    }

    @Override public Thread newThread(Runnable r) {
        final String name = prefix + atomicInteger.getAndIncrement();
        return new WxdThread(name, r);
    }

    public static class WxdThread extends Thread {

        final Runnable r;

        public WxdThread(String name, Runnable r) {
            super(name);
            this.r = r;
        }

        @Override public void run() {
            while (true) {
                try {
                    r.run();
                    break;
                } catch (Throwable throwable) {
                    if (throwable instanceof InterruptedException) {break;}
                    GlobalUtil.exception("线程【" + this.getName() + "】异常", throwable);
                }
            }
        }
    }

}
