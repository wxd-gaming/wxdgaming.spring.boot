package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.GlobalUtil;
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

    @Override public final void run() {
        run0();
    }

    protected void run0() {
        GuardThread.ins.push(this);
        try {
            try {
                if (threadContext != null)
                    ThreadContext.set(threadContext);
                onEvent();
            } catch (Throwable throwable) {
                GlobalUtil.exception(this.getClass().getName(), throwable);
            } finally {
                ThreadContext.cleanup();
            }
        } finally {
            GuardThread.ins.release();
        }
    }

    /** 事件执行器 */
    protected abstract void onEvent() throws Throwable;

}
