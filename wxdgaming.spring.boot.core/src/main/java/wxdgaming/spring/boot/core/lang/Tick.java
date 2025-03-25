package wxdgaming.spring.boot.core.lang;

import lombok.Getter;
import wxdgaming.spring.boot.core.timer.MyClock;

import java.util.concurrent.TimeUnit;

/**
 * 定时器处理，间隔多少心跳什么的
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-13 10:26
 **/
@Getter
public class Tick extends ObjectBase {

    /** 同步等待的时候自循环等待 */
    private final long heart;
    /** 间隔毫秒 */
    private final long interval;
    /** 上一次执行时间 */
    private volatile long last = 0;

    public Tick(long interval) {
        this(interval, TimeUnit.MILLISECONDS);
    }

    public Tick(long duration, TimeUnit timeUnit) {
        this(50, duration, timeUnit);
    }

    public Tick(long heart, long duration, TimeUnit timeUnit) {
        this(heart, timeUnit.toMillis(duration), MyClock.millis());
    }

    public Tick(long heart, long duration, long last) {
        this.heart = heart;
        this.interval = duration;
        this.last = last;
        if (heart > this.interval)
            throw new RuntimeException("自循环心跳 heart=" + heart + " 大于间隔执行 interval=" + this.interval);
    }

    public void reset() {
        last = MyClock.millis();
    }

    /** 判断是否满足条件，如果满足条件自动更新  返回间隔毫秒数 */
    public boolean need() {
        long millis = MyClock.millis();
        return need(millis);
    }

    /** 判断是否满足条件，如果满足条件自动更新 返回间隔毫秒数 */
    public boolean need(long now) {
        long nanoTime = now - last;
        if (nanoTime >= interval) {
            last = now;
            return true;
        }
        return false;
    }

    /** 同步等待 */
    public void waitNext() {
        try {
            while (!Thread.currentThread().isInterrupted() && !need()) {
                Thread.sleep(heart);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
