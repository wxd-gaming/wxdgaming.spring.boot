package wxdgaming.spring.boot.core.threading;

import lombok.Getter;
import wxdgaming.spring.boot.core.GlobalUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 队列事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 15:04
 **/
@Getter
public class QueueEvent implements Event, Executor {

    private final BaseExecutor executor;
    private final BlockingQueue<Runnable> runnableBlockingQueue;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    /**
     * new 单线程执行器，因为队列事件，必然是单线程的
     *
     * @param threadPrefix 线程名称
     */
    public QueueEvent(String threadPrefix) {
        this(threadPrefix, 3000);
    }

    /**
     * new 单线程执行器，因为队列事件，必然是单线程的
     *
     * @param threadPrefix 线程名称
     * @param queueMaxSize 队列最大长度
     */
    public QueueEvent(String threadPrefix, int queueMaxSize) {
        this(new BaseExecutor(threadPrefix, 1), queueMaxSize);
    }

    public QueueEvent(BaseExecutor executor) {
        this(executor, 3000);
    }

    public QueueEvent(BaseExecutor executor, int queueMaxSize) {
        this.executor = executor;
        this.runnableBlockingQueue = new LinkedBlockingQueue<>(queueMaxSize);
    }

    @Override public void onEvent() throws Throwable {
        Runnable poll = null;
        try {
            poll = runnableBlockingQueue.poll();
            if (poll != null) {
                poll.run();
            }
        } catch (Throwable throwable) {
            GlobalUtil.exception(poll.getClass().getName(), throwable);
        }
        try {
            reentrantLock.lock();
            if (runnableBlockingQueue.isEmpty()) {
                atomicBoolean.set(false);
            } else {
                this.executor.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override public void execute(Runnable command) {
        Event event;
        if (!(command instanceof Event)) {
            event = new RunEvent(command);
        } else {
            event = (Event) command;
        }
        runnableBlockingQueue.add(event);
        try {
            reentrantLock.lock();
            if (atomicBoolean.compareAndSet(false, true)) {
                this.executor.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

}
