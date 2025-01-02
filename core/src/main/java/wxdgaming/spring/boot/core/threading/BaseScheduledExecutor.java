package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.InitPrint;

import java.util.List;
import java.util.concurrent.*;

/**
 * logic Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
public class BaseScheduledExecutor extends ScheduledThreadPoolExecutor implements Executor, ScheduledExecutorService, InitPrint {

    public BaseScheduledExecutor(String prefix, int coreSize) {
        super(coreSize, new WxdThreadFactory(prefix));
        ExecutorService.put(prefix, this);
    }

    @Override protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override public void close() {
        super.close();
    }

    @Override public Future<?> submit(Runnable task) {
        return super.submit(RunEvent.of(4, task));
    }

    @Override public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(RunEvent.of(4, task), result);
    }

    @Override public <T> Future<T> submit(Callable<T> task) {
        EventCallable<T> eventCallable = new EventCallable<>(5) {
            @Override public T call() throws Exception {
                return task.call();
            }
        };
        super.execute(eventCallable);
        return eventCallable.future;
    }

    public <T> CompletableFuture<T> submit(EventCallable<T> task) {
        super.execute(task);
        return task.future;
    }

    public void execute(String queueName, Runnable task) {
        EventQueue eventQueueOrNew = ExecutorService.getEventQueueOrNew(queueName, this);
        eventQueueOrNew.execute(task);
    }

    @Override public void execute(Runnable command) {
        super.execute(RunEvent.of(4, command));
    }

    /** 延迟任务 */
    @Override public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(RunEvent.of(4, command), delay, unit);
    }

    /** 延迟任务 */
    @Override public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return super.schedule(callable, delay, unit);
    }

    /** 间隔执行任务，无限期循环，当上一次没有执行完成 依然会执行下一次，只跟间隔时间有关系 */
    @Override public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(RunEvent.of(4, command), initialDelay, period, unit);
    }

    /** 间隔执行任务，无限期循环，当上一次没有执行完成不会执行下一次 */
    @Override public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(RunEvent.of(4, command), initialDelay, delay, unit);
    }

    @Override public void shutdown() {
        super.shutdown();
    }

    @Override public List<Runnable> shutdownNow() {
        return super.shutdownNow();
    }

}
