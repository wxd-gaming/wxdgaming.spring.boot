package wxdgaming.spring.boot.core.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.spring.boot.core.GlobalUtil;
import wxdgaming.spring.boot.core.util.AssertUtil;
import wxdgaming.spring.boot.core.util.StringUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public interface IExecutorServices extends Executor {

    Logger log = LoggerFactory.getLogger(IExecutorServices.class);

    /** 线程池名称 */
    String getName();

    /** 当前线程池核心线程数量 */
    int getCoreSize();

    /** 当前线程池最大线程数量 */
    int getMaxSize();

    /** 当前线程池触发警告队列长度 */
    int getQueueCheckSize();

    /** 当前线程池队列是否为空 */
    boolean isQueueEmpty();

    /** 当前线程池队列长度 */
    int queueSize();

    /** 关闭 */
    void shutdown();

    /** 关闭 */
    List<Runnable> terminate();

    boolean isShutdown();

    boolean isTerminated();

    ConcurrentHashMap<String, ExecutorQueue> getExecutorQueueMap();

    /** 提交到线程 */
    void threadPoolExecutor(Runnable command);

    /** 线程池队列 */
    BlockingQueue<Runnable> threadPoolQueue();

    /** 普通任务 */
    @Override default void execute(Runnable runnable) {
        String queueName = null;
        if (runnable instanceof Event event) {
            queueName = event.getQueueName();
        }
        int stackTrace = 3;
        if (runnable instanceof ForkJoinTask)
            stackTrace = 6;
        submit(queueName, runnable, stackTrace);
    }

    /** 普通任务 */
    default Job submit(Runnable runnable) {
        String queueName = null;
        if (runnable instanceof Event event) {
            queueName = event.getQueueName();
        }
        return submit(queueName, runnable, 3);
    }

    /** 普通任务 */
    default Job submit(Runnable runnable, int stackTrace) {
        String queueName = null;
        if (runnable instanceof Event event) {
            queueName = event.getQueueName();
        }
        return submit(queueName, runnable, stackTrace);
    }

    /** 队列任务 */
    default Job submit(String queueName, Runnable command) {
        return submit(queueName, command, 3);
    }

    /**
     * 队列任务
     *
     * @param queueName  队列名
     * @param runnable   需要执行的任务
     * @param stackTrace 记录来源堆栈层级
     * @return
     */
    default Job submit(String queueName, Runnable runnable, int stackTrace) {
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, runnable, stackTrace);
        executeJob(queueName, executorServiceJob);
        return executorServiceJob;
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(Supplier<U> supplier) {
        return optFuture0("", supplier, "", 66, 150, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(Supplier<U> supplier, int stackTrace) {
        return optFuture0("", supplier, "", 66, 150, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(Supplier<U> supplier, long logTime, long warningTime) {
        return optFuture0("", supplier, "", logTime, warningTime, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(Supplier<U> supplier, String taskInfoString, long logTime, long warningTime, int stackTrace) {
        return optFuture0("", supplier, taskInfoString, logTime, warningTime, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(String queueName, Supplier<U> supplier) {
        return optFuture0(queueName, supplier, "", 66, 150, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(String queueName, Supplier<U> supplier, int stackTrace) {
        return optFuture0(queueName, supplier, "", 66, 150, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(String queueName, Supplier<U> supplier, long logTime, long warningTime, int stackTrace) {
        return optFuture0(queueName, supplier, "", logTime, warningTime, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture(String queueName, Supplier<U> supplier, String taskInfoString, long logTime, long warningTime, int stackTrace) {
        return optFuture0(queueName, supplier, taskInfoString, logTime, warningTime, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> optFuture0(String queueName, Supplier<U> supplier, String taskInfoString, long logTime, long warningTime, int stackTrace) {
        CompletableFuture<U> empty = new CompletableFuture<>();
        submit(queueName, new Event(taskInfoString, logTime, warningTime) {
            @Override public void onEvent() throws Exception {
                try {
                    empty.complete(supplier.get());
                } catch (Throwable throwable) {
                    empty.completeExceptionally(throwable);
                }
            }
        }, stackTrace);
        return empty;
    }

    /** 提交带回调的执行 */
    default CompletableFuture<Void> completableFuture(Runnable runnable) {
        return completableFuture("", runnable, "", 66, 150, 4);
    }

    /** 提交带回调的执行 */
    default CompletableFuture<Void> completableFuture(Runnable runnable, String taskInfoString, long logTime, long warningTime) {
        return completableFuture("", runnable, taskInfoString, logTime, warningTime, 4);
    }

    /** 提交带回调的执行 */
    default CompletableFuture<Void> completableFuture(String queueName, Runnable runnable) {
        return completableFuture(queueName, runnable, "", 66, 150, 4);
    }

    /** 提交带回调的执行 */
    default CompletableFuture<Void> completableFuture(Runnable runnable, int stackTrace) {
        return completableFuture("", runnable, "", 66, 150, stackTrace);
    }

    /** 提交带回调的执行 */
    default CompletableFuture<Void> completableFuture(String queueName, Runnable runnable, String taskInfoString, long logTime, long warningTime, int stackTrace) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        this.submit(queueName, new Event(taskInfoString, logTime, warningTime) {
            @Override public void onEvent() throws Exception {
                try {
                    runnable.run();
                    completableFuture.complete(null);
                } catch (Throwable throwable) {
                    completableFuture.completeExceptionally(throwable);
                }
            }
        }, stackTrace);
        return completableFuture;
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(Supplier<U> supplier) {
        return completableFuture("", supplier, "", 66, 150, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(Supplier<U> supplier, int stackTrace) {
        return completableFuture("", supplier, "", 66, 150, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(String queueName, Supplier<U> supplier) {
        return completableFuture(queueName, supplier, "", 66, 150, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(String queueName, Supplier<U> supplier, int stackTrace) {
        return completableFuture(queueName, supplier, "", 66, 150, stackTrace);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(Supplier<U> supplier, long logTime, long warningTime) {
        return completableFuture("", supplier, "", logTime, warningTime, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(String queueName, Supplier<U> supplier, long logTime, long warningTime) {
        return completableFuture(queueName, supplier, "", logTime, warningTime, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(String queueName, Supplier<U> supplier,
                                                       String taskInfoString, long logTime, long warningTime) {
        return completableFuture(queueName, supplier, taskInfoString, logTime, warningTime, 4);
    }

    /** 提交带回调的执行 */
    default <U> CompletableFuture<U> completableFuture(String queueName, Supplier<U> supplier,
                                                       String taskInfoString, long logTime, long warningTime,
                                                       int stackTrace) {
        CompletableFuture<U> completableFuture = new CompletableFuture<>();
        this.submit(queueName, new Event(taskInfoString, logTime, warningTime) {
            @Override public void onEvent() throws Exception {
                try {
                    U u = supplier.get();
                    completableFuture.complete(u);
                } catch (Throwable throwable) {
                    completableFuture.completeExceptionally(throwable);
                }
            }
        }, stackTrace);
        return completableFuture;
    }

    /** 执行一次的延时任务 */
    default TimerJob schedule(Runnable command, long delay, TimeUnit unit) {
        return schedule(command, delay, unit, 3);
    }

    /** 执行一次的延时任务 */
    default TimerJob schedule(Runnable command, long delay, TimeUnit unit, int stackTrace) {
        String queueName = null;
        if (command instanceof Event event) {
            queueName = event.getQueueName();
        }
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, command, stackTrace);
        TimerJob timerJob = new TimerJob(this, queueName, executorServiceJob, delay, delay, unit, 1);
        ExecutorUtil.getInstance().TIMER_THREAD.add(timerJob);
        return timerJob;
    }

    /** 依赖队列的执行一次的延时任务 */
    default TimerJob schedule(String queueName, Runnable command, long delay, TimeUnit unit) {
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, command, 2);
        TimerJob timerJob = new TimerJob(this, queueName, executorServiceJob, delay, delay, unit, 1);
        ExecutorUtil.getInstance().TIMER_THREAD.add(timerJob);
        return timerJob;
    }

    /** 定时运行可取消的周期性任务 上一次没有执行，不会执行第二次，等待上一次执行完成 */
    default TimerJob scheduleAtFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        String queueName = null;
        if (command instanceof Event event) {
            queueName = event.getQueueName();
        }
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, command, 2);
        TimerJob timerJob = new TimerJob(this, queueName, executorServiceJob, initialDelay, delay, unit, -1);
        ExecutorUtil.getInstance().TIMER_THREAD.add(timerJob);
        return timerJob;
    }

    /** 定时运行可取消的周期性任务 上一次没有执行，不会执行第二次，等待上一次执行完成 */
    default TimerJob scheduleAtFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit, int execCount) {
        return scheduleAtFixedDelay(command, initialDelay, delay, unit, execCount, 3);
    }

    /** 依赖队列 定时运行可取消的周期性任务 上一次没有执行，不会执行第二次，等待上一次执行完成 */
    default TimerJob scheduleAtFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit, int execCount, int stackTrace) {
        String queueName = null;
        if (command instanceof Event event) {
            queueName = event.getQueueName();
        }
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, command, stackTrace);
        TimerJob timerJob = new TimerJob(this, queueName, executorServiceJob, initialDelay, delay, unit, execCount);
        ExecutorUtil.getInstance().TIMER_THREAD.add(timerJob);
        return timerJob;
    }

    /** 依赖队列 定时运行可取消的周期性任务 上一次没有执行，不会执行第二次，等待上一次执行完成 */
    default TimerJob scheduleAtFixedDelay(String queueName, Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, command, 2);
        TimerJob timerJob = new TimerJob(this, queueName, executorServiceJob, initialDelay, delay, unit, -1);
        ExecutorUtil.getInstance().TIMER_THREAD.add(timerJob);
        return timerJob;
    }

    /** 依赖队列 定时运行可取消的周期性任务 上一次没有执行，不会执行第二次，等待上一次执行完成 */
    default TimerJob scheduleAtFixedDelay(String queueName, Runnable command, long initialDelay, long delay, TimeUnit unit, int execCount) {
        return scheduleAtFixedDelay(queueName, command, initialDelay, delay, unit, execCount, 3);
    }

    /** 依赖队列 定时运行可取消的周期性任务 上一次没有执行，不会执行第二次，等待上一次执行完成 */
    default TimerJob scheduleAtFixedDelay(String queueName, Runnable command, long initialDelay, long delay, TimeUnit unit, int execCount, int stackTrace) {
        ExecutorServiceJob executorServiceJob = new ExecutorServiceJob(this, command, stackTrace);
        TimerJob timerJob = new TimerJob(this, queueName, executorServiceJob, initialDelay, delay, unit, execCount);
        ExecutorUtil.getInstance().TIMER_THREAD.add(timerJob);
        return timerJob;
    }

    default void executeJob(String queueName, ExecutorServiceJob job) {
        AssertUtil.assertTrue(!isTerminated(), getName() + " 线程已经关闭 " + job);
        AssertUtil.assertTrue(!isShutdown(), getName() + " 线程正在关闭 " + job);

        job.queueName = queueName;
        /*TODO 定时器任务，需要重置一次*/
        job.initTaskTime = System.nanoTime();
        job.append.set(true);
        if (StringUtils.isNotBlank(queueName)) {
            ExecutorQueue executorQueue = getExecutorQueueMap().computeIfAbsent(queueName, k -> new ExecutorQueue(this, k));
            executorQueue.add(job);
        } else {
            threadPoolExecutor(job);
            int queueSize = queueSize();
            if (queueSize > getQueueCheckSize()) {
                RuntimeException throwable = new RuntimeException();
                GlobalUtil.exception(getName() + " 任务剩余过多 主队列 size：" + queueSize, throwable);
            }
        }
    }

}
