package wxdgaming.spring.boot.starter.core.threading;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.GlobalUtil;
import wxdgaming.spring.boot.starter.core.util.StringUtils;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 线程执行类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-11-10 22:45
 **/
@Slf4j
@Getter
public class ExecutorServiceJob implements Runnable, Job {

    protected final IExecutorServices iExecutorServices;
    protected String runName = "";
    protected volatile String queueName = "";
    protected final Runnable task;
    protected final ThreadContext threadContext;
    protected volatile long initTaskTime;
    protected volatile long startExecTime;
    protected volatile Thread currentThread;
    /** 是否已经追加到队列 */
    protected AtomicBoolean append = new AtomicBoolean();

    public ExecutorServiceJob(IExecutorServices iExecutorServices, Runnable task, int stackTrace) {
        this.iExecutorServices = iExecutorServices;
        this.task = task;
        if (task instanceof Event event) {
            this.runName = event.getTaskInfoString();
        }
        if (StringUtils.isBlank(runName)) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[stackTrace + 1];
            this.runName = stackTraceElement.getClassName()
                           + "." + stackTraceElement.getMethodName()
                           + "():line：" + stackTraceElement.getLineNumber();
        }
        threadContext = new ThreadContext(ThreadContext.context());
    }

    public void check(StringBuilder stringBuilder) {

        long warningTime = 5000;

        if (task instanceof RunMonitor runMonitor) {
            warningTime = runMonitor.getWarningTime();
        }

        long procc = (System.nanoTime() - startExecTime) / 10000 / 100;
        if (procc > warningTime && procc < 864000000L) {
            /*小于10天//因为多线程操作时间可能不准确*/
            /*如果线程卡住，锁住，暂停，*/
            if (currentThread.getState() == Thread.State.BLOCKED
                || currentThread.getState() == Thread.State.RUNNABLE
                || currentThread.getState() == Thread.State.TIMED_WAITING
                || currentThread.getState() == Thread.State.WAITING) {
                stringBuilder.append("线程[").append(currentThread.toString()).append("] 状态[")
                        .append(currentThread.getState()).append("]").append("\n ")
                        .append("执行任务：").append(this.runName)
                        .append(" 耗时 -> ")
                        .append(procc / 1000f)
                        .append(" 秒    ");
                try {
                    StackTraceElement[] elements = currentThread.getStackTrace();
                    for (int i = 0; i < elements.length; i++) {
                        stringBuilder.append("\n        ")
                                .append(elements[i].getClassName())
                                .append(".")
                                .append(elements[i].getMethodName())
                                .append("(").append(elements[i].getFileName())
                                .append(";")
                                .append(elements[i].getLineNumber()).append(")");
                    }
                } catch (Throwable e) {
                    stringBuilder.append(e);
                }
                stringBuilder.append("\n++++++++++++++++++++++++++++++++++");
            }
        }

    }

    @Override public void run() {

        currentThread = Thread.currentThread();
        startExecTime = System.nanoTime();
        ExecutorUtilImpl.getInstance().Run_THREAD_LOCAL.put(currentThread, this);
        ExecutorUtilImpl.getInstance().CurrentThread.set(this);

        try {
            ThreadContext.set(threadContext);
            try {
                task.run();
            } finally {
                ThreadContext.cleanup();
            }
            float v = (System.nanoTime() - startExecTime) / 10000 / 100f;
            float v2 = (System.nanoTime() - initTaskTime) / 10000 / 100f;

            long logTime = 30;
            long warningTime = 1000;

            if (task instanceof ForkJoinTask) {
                logTime = 300;
                warningTime = 5000;
            }

            if (task instanceof RunMonitor runMonitor) {
                logTime = runMonitor.getLogTime();
                warningTime = runMonitor.getWarningTime();
            }

            if (v > logTime) {

                String msg = "执行：" + runName + ", 耗时：" + v + " ms, 创建到执行完成：" + v2 + " ms, 主队列剩余：" + iExecutorServices.queueSize();
                if (StringUtils.isNotBlank(queueName)) {
                    msg = "子队列：" + queueName + ", 剩余" + iExecutorServices.getExecutorQueueMap().get(queueName).size() + "; " + msg;
                }

                if (v > logTime || v2 > logTime) {
                    log.info(msg);
                    if (v > warningTime || v2 > warningTime) {
                        // GlobalUtil.exception(msg, null);
                    }
                }
            }

        } catch (Throwable throwable) {
            GlobalUtil.exception("执行：" + runName, throwable);
        } finally {
            ExecutorUtilImpl.getInstance().CurrentThread.remove();
            ExecutorUtilImpl.getInstance().Run_THREAD_LOCAL.remove(currentThread);
            this.append.set(false);
        }
    }

    @Override public String names() {
        return toString();
    }

    @Override public boolean cancel() {
        if (StringUtils.isNotBlank(queueName)) {
            return iExecutorServices.getExecutorQueueMap().computeIfAbsent(queueName, k -> new ExecutorQueue(iExecutorServices, k)).remove(this);
        } else {
            return iExecutorServices.threadPoolQueue().remove(this);
        }
    }

    @Override public String toString() {
        return runName;
    }

}
