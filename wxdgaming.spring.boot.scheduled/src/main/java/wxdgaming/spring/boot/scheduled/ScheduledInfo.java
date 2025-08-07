package wxdgaming.spring.boot.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.Const;
import wxdgaming.spring.boot.core.assist.JavassistProxy;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.IExecutorQueue;
import wxdgaming.spring.boot.core.io.Objects;
import wxdgaming.spring.boot.core.reflect.AnnUtil;
import wxdgaming.spring.boot.core.reflect.MethodUtil;
import wxdgaming.spring.boot.core.timer.CronExpress;
import wxdgaming.spring.boot.core.util.GlobalUtil;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * cron 表达式时间触发器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-09-27 10:40
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class ScheduledInfo extends ExecutorEvent implements Runnable, IExecutorQueue, Comparable<ScheduledInfo> {

    protected String name;
    protected int index;
    protected final Object instance;
    protected final Method method;
    protected JavassistProxy scheduledProxy;
    protected final CronExpress cronExpress;
    /** 上一次执行尚未完成是否持续执行 默认false 不执行 */
    protected final boolean scheduleAtFixedRate;
    protected final ReentrantLock lock = new ReentrantLock();
    protected final AtomicBoolean runEnd = new AtomicBoolean(true);
    protected long nextRunTime = -1;

    public ScheduledInfo(Object instance, Method method, Scheduled scheduled) {
        super(method);
        this.instance = instance;
        this.method = method;

        scheduledProxy = JavassistProxy.of(instance, method);

        if (StringUtils.isNotBlank(scheduled.name())) {
            this.name = "[scheduled-job] " + scheduled.name();
        } else {
            this.name = "[scheduled-job] " + instance.getClass().getName() + "." + method.getName();
        }

        final Order orderAnn = AnnUtil.ann(method, Order.class);
        this.index = orderAnn == null ? Const.SORT_DEFAULT : orderAnn.value();
        this.scheduleAtFixedRate = scheduled.scheduleAtFixedRate();
        this.cronExpress = new CronExpress(scheduled.value(), TimeUnit.SECONDS, 0);
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

    @Override public void onEvent() throws Exception {
        try {
            scheduledProxy.proxyInvoke(Objects.ZERO_ARRAY);
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

    @Override
    public int compareTo(ScheduledInfo o) {
        return Integer.compare(this.index, o.index);
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ScheduledInfo that = (ScheduledInfo) o;
        return getInstance().getClass().getName().equals(that.getInstance().getClass().getName())
               && MethodUtil.methodFullName(getMethod()).equals(MethodUtil.methodFullName(that.getMethod()));
    }

    @Override public int hashCode() {
        int result = getInstance().hashCode();
        result = 31 * result + getMethod().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

}
