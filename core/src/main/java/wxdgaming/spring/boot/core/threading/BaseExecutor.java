package wxdgaming.spring.boot.core.threading;

import java.util.List;
import java.util.concurrent.*;

/**
 * logic Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
public class BaseExecutor extends ThreadPoolExecutor {

    public BaseExecutor(String prefix, int coreSize, int maximumPoolSize, int queueSize) {
        super(coreSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize),
                new WxdThreadFactory(prefix)
        );
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
        return super.submit(RunEvent.of(task));
    }

    @Override public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(RunEvent.of(task), result);
    }

    @Override public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }

    @Override public void execute(Runnable command) {
        super.execute(RunEvent.of(command));
    }

    @Override public void shutdown() {
        super.shutdown();
    }

    @Override public List<Runnable> shutdownNow() {
        return super.shutdownNow();
    }

}
