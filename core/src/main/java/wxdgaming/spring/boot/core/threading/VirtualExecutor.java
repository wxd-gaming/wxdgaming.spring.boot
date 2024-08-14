package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.lang.LockBase;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * logic Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
public class VirtualExecutor extends LockBase implements Executor {

    /** 用于控制并发数量 */
    private final int coreSize;
    /** 当前正在执行的任务数量 */
    private final AtomicInteger curCount = new AtomicInteger(0);
    /** 当前虚拟线程池的队列 */
    private final BlockingQueue<VirtualEvent> runnableBlockingQueue;
    Thread.Builder.OfVirtual virtual;

    public VirtualExecutor(int coreSize) {
        this.coreSize = coreSize;
        this.runnableBlockingQueue = new ArrayBlockingQueue<>(30000);
        virtual = Thread.ofVirtual().name("virtual-", 1);

    }

    @Override public void execute(Runnable command) {
        VirtualEvent virtualEvent = new VirtualEvent(command);
        lock();
        try {
            if (curCount.get() >= coreSize) {
                runnableBlockingQueue.add(virtualEvent);
            } else {
                curCount.incrementAndGet();
                virtual.start(virtualEvent);
            }
        } finally {
            unlock();
        }
    }

    public <R> CompletableFuture<R> submit(Supplier<R> supplier) {
        final CompletableFuture<R> future = new CompletableFuture<>();
        execute(() -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    class VirtualEvent implements Event {

        final Runnable command;

        public VirtualEvent(Runnable command) {
            this.command = command;
        }

        @Override public void onEvent() throws Throwable {
            try {
                command.run();
            } finally {
                lock();
                try {
                    if (runnableBlockingQueue.isEmpty()) {
                        curCount.decrementAndGet();
                    } else {
                        VirtualEvent poll = runnableBlockingQueue.poll();
                        virtual.start(poll);
                    }
                } finally {
                    unlock();
                }
            }
        }

    }

}
