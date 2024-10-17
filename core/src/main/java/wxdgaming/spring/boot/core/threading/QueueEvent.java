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
public class QueueEvent extends Event implements Executor {

    private final String queueName;
    private final Executor executor;
    private final BlockingQueue<Event> runnableBlockingQueue;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    /**
     * new 单线程执行器，因为队列事件，必然是单线程的
     *
     * @param threadPrefix 线程名称
     */
    public QueueEvent(String queueName, String threadPrefix) {
        this(queueName, new BaseExecutor(threadPrefix, 1, 1, 1), 3000);
    }

    /**
     * new 单线程执行器，因为队列事件，必然是单线程的
     *
     * @param threadPrefix 线程名称
     * @param queueMaxSize 队列最大长度
     */
    public QueueEvent(String queueName, String threadPrefix, int queueMaxSize) {
        this(queueName, new BaseExecutor(threadPrefix, 1, 1, 1), queueMaxSize);
    }

    public QueueEvent(String queueName, Executor executor) {
        this(queueName, executor, 3000);
    }

    public QueueEvent(String queueName, Executor executor, int queueMaxSize) {
        this.queueName = queueName;
        this.executor = executor;
        this.runnableBlockingQueue = new LinkedBlockingQueue<>(queueMaxSize);
    }

    Event curPoll = null;

    @Override public void onEvent() throws Throwable {
        try {
            curPoll = runnableBlockingQueue.poll();
            if (curPoll != null) {
                curPoll.run0();
            }
        } catch (Throwable throwable) {
            GlobalUtil.exception(String.valueOf(curPoll), throwable);
        } finally {
            curPoll = null;
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
        Event event = RunEvent.of(command);
        event.queueName = queueName;
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
