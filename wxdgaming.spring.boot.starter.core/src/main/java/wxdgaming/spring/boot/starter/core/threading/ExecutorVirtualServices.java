package wxdgaming.spring.boot.starter.core.threading;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 线程执行器
 * <p>
 * 禁止使用 synchronized 同步锁
 * <p>
 * 直接线程池，每一个任务都会new Virtual Thread
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-31 20:36
 **/
@Slf4j
@Getter
public final class ExecutorVirtualServices implements Executor, IExecutorServices {

    /** 执行器 */
    final VirtualThreadExecutors executors;
    /** 队列任务 */
    final ConcurrentHashMap<String, ExecutorQueue> executorQueueMap = new ConcurrentHashMap<>();
    /** 当队列执行数量剩余过多的预警 */
    final int queueCheckSize;

    /**
     * @param name     线程池名称
     * @param coreSize 线程核心数量
     * @param coreSize 线程最大数量
     * @return
     */
    ExecutorVirtualServices(String name, int coreSize, int maxSize, int queueCheckSize) {
        if (!name.startsWith("vt-"))
            name = "vt-" + name;
        if (ExecutorUtilImpl.getInstance().All_THREAD_LOCAL.containsKey(name)) {
            throw new RuntimeException("已经存在线程池：" + name);
        }
        ExecutorUtilImpl.getInstance().All_THREAD_LOCAL.put(name, this);
        executors = new VirtualThreadExecutors(
                name,
                coreSize,
                maxSize,
                new LinkedBlockingQueue<>()
        );
        this.queueCheckSize = queueCheckSize;
    }

    /** 线程池名字 */
    @Override public String getName() {
        return this.executors.getName();
    }

    @Override public int getCoreSize() {
        return this.executors.getCoreSize();
    }

    @Override public int getMaxSize() {
        return this.executors.getCoreSize();
    }

    /** 当前线程池剩余未处理队列 */
    @Override public boolean isQueueEmpty() {
        return executors.getQueue().isEmpty();
    }

    /** 当前线程池剩余未处理队列 */
    @Override public int queueSize() {
        return executors.getQueue().size();
    }

    /** 准备关闭，不再接受新的任务 ,并且会等待当前队列任务全部执行完成 */
    @Override public void shutdown() {
        executors.shutdown();
    }

    /** 即将关闭线程，不再接受新任务，并且线程当前任务执行完成不再执行队列里面的任务 */
    @Override public List<Runnable> terminate() {
        return executors.terminate();
    }

    /** 已关闭 */
    @Override public boolean isShutdown() {
        return executors.isShutdown();
    }

    /** 已终止 */
    @Override public boolean isTerminated() {
        return executors.isTerminated();
    }

    @Override public int getQueueCheckSize() {
        return this.queueCheckSize;
    }

    @Override public ConcurrentHashMap<String, ExecutorQueue> getExecutorQueueMap() {
        return this.executorQueueMap;
    }

    @Override public BlockingQueue<Runnable> threadPoolQueue() {
        return this.executors.getQueue();
    }

    @Override public void threadPoolExecutor(Runnable command) {
        this.executors.execute(command);
    }

}
