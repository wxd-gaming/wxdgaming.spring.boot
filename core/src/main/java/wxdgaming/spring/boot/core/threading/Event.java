package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.GlobalUtil;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.util.StringsUtil;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 15:02
 **/
public abstract class Event implements Runnable {
    /** 事件所在的队列 */
    String queueName;
    /** 事件名 */
    String runName;
    /** 事件开始执行的时间 */
    volatile long execStartTime;
    /** 当前事件的上下文 */
    final ThreadContext threadContext;

    public Event() {
        this(3);
    }

    public Event(int stackTrace) {
        ThreadContext context = ThreadContext.contextOrNull();
        if (context != null) {
            threadContext = new ThreadContext(context);
        } else {
            threadContext = null;
        }
        if (StringsUtil.emptyOrNull(runName())) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[stackTrace];
            this.runName = stackTraceElement.getClassName()
                           + "." + stackTraceElement.getMethodName()
                           + "():line：" + stackTraceElement.getLineNumber();
        } else {
            this.runName = runName();
        }
    }

    public String runName() {
        return null;
    }

    void check(StringBuilder sb, Thread thread) {
        if (execStartTime > 0) {
            long nanoTime = System.nanoTime() - execStartTime;
            float diff = nanoTime / 10000 / 100f;
            if (diff > 1000) {
                sb.append("\n");
                sb.append("线程：").append(thread.getName()).append("\n");
                if (StringsUtil.notEmptyOrNull(queueName)) {
                    sb.append("队列：").append(queueName).append("\n");
                }
                sb.append("任务：").append(runName).append("\n");
                sb.append("耗时：").append(String.format("%3f", diff)).append(" ms\n");
                Throw.ofString(sb, thread.getStackTrace(), true);
            }
        }
    }

    @Override public final void run() {
        GuardThread.ins.eventMap.put(Thread.currentThread(), this);
        try {
            run0();
        } finally {
            GuardThread.ins.eventMap.remove(Thread.currentThread());
        }
    }

    final void run0() {
        try {
            execStartTime = System.nanoTime();
            if (threadContext != null)
                ThreadContext.set(threadContext);
            onEvent();
        } catch (Throwable throwable) {
            GlobalUtil.exception(this.getClass().getName(), throwable);
        } finally {
            ThreadContext.cleanup();
            execStartTime = 0;
        }
    }

    /** 事件执行器 */
    public abstract void onEvent() throws Throwable;

}
