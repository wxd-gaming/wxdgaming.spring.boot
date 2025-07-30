package wxdgaming.spring.boot.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.Const;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.IExecutorQueue;
import wxdgaming.spring.boot.core.timer.CronExpress;
import wxdgaming.spring.boot.core.util.GlobalUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * cron 表达式时间触发器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-09-27 10:40
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public abstract class CronTrigger extends ExecutorEvent implements Runnable, IExecutorQueue, Comparable<CronTrigger> {

    protected String name;
    protected int index;
    protected final CronExpress cronExpress;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;
    protected final ReentrantLock lock = new ReentrantLock();
    protected final AtomicBoolean runEnd = new AtomicBoolean(true);
    protected long nextRunTime = -1;

    /**
     * 秒 分 时 日 月 星期 年
     * <p> {@code * * * * * * * }
     * <p> 下面以 秒 配置举例
     * <p> * 或者 ? 无限制,
     * <p> 数字是 指定秒执行
     * <p> 0-5 第 0 秒 到 第 5 秒执行 每秒执行
     * <p> 0,5 第 0 秒 和 第 5 秒 各执行一次
     * <p> {@code *}/5 秒 % 5 == 0 执行
     * <p> 5/5 第五秒之后 每5秒执行一次
     * <p> 秒 0-59
     * <p> 分 0-59
     * <p> 时 0-23
     * <p> 日 1-28 or 29 or 30 or 31
     * <p> 月 1-12
     * <p> 星期 1-7 Mon Tues Wed Thur Fri Sat Sun
     * <p> 年 1970 - 2199
     */
    public CronTrigger(String scheduledName, String cron, boolean scheduleAtFixedRate) {
        this.name = "[timer-job] " + this.getClass() + "-" + scheduledName;

        this.index = Const.SORT_DEFAULT;
        this.scheduleAtFixedRate = scheduleAtFixedRate;
        this.cronExpress = new CronExpress(cron, TimeUnit.SECONDS, 0);
    }

    public long getNextRunTime() {
        if (nextRunTime == -1) {
            this.nextRunTime = this.cronExpress.validateTimeAfterMillis();
        }
        return nextRunTime;
    }

    /** 检查时间是否满足 */
    public boolean checkRunTime(long millis) {
        return millis >= getNextRunTime();
    }

    public boolean isAsync() {
        return executorWith != null;
    }

    @Override public String getStack() {
        return this.name;
    }

    @Override public final void onEvent() throws Exception {
        try {
            trigger();
        } catch (Throwable throwable) {
            String msg = "执行：" + this.name;
            GlobalUtil.exception(msg, throwable);
        } finally {
            lock.lock();
            try {
                /*标记为执行完成*/
                runEnd.set(true);
            } finally {
                lock.unlock();
            }
        }
    }

    public abstract void trigger();

    @Override
    public int compareTo(CronTrigger o) {
        return Integer.compare(this.index, o.index);
    }


    @Override
    public String toString() {
        return name;
    }

}
