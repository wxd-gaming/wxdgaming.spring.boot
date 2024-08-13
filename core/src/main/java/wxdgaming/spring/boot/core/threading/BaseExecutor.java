package wxdgaming.spring.boot.core.threading;

import lombok.NonNull;

import java.util.List;
import java.util.concurrent.*;

/**
 * logic Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
public class BaseExecutor extends ScheduledThreadPoolExecutor {

    public BaseExecutor(String prefix, int coreSize) {
        super(coreSize, new ThreadNameFactory(prefix));
    }

    @Override protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (r instanceof QueueEvent) {
            this.execute(r);
        }
    }

    @Override public void close() {
        super.close();
    }

    @Override public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(task, result);
    }

    @NonNull
    @Override public <T> Future<T> submit(@NonNull Callable<T> task) {
        return super.submit(task);
    }

    @Override public void execute(Runnable command) {
        super.execute(command);
    }

    @Override public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return super.schedule(command, delay, unit);
    }

    @Override public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return super.schedule(callable, delay, unit);
    }

    /** 间隔执行任务，无限期循环，当上一次没有执行完成 依然会执行下一次，只跟间隔时间有关系 */
    @Override public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /** 间隔执行任务，无限期循环，当上一次没有执行完成不会执行下一次 */
    @Override public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    @Override public void shutdown() {
        super.shutdown();
    }

    @Override public List<Runnable> shutdownNow() {
        return super.shutdownNow();
    }

}
