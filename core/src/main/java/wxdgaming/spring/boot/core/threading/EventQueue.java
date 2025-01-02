package wxdgaming.spring.boot.core.threading;

import lombok.Getter;
import wxdgaming.spring.boot.core.GlobalUtil;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 队列事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 15:04
 **/
@Getter
public class EventQueue extends Event implements Executor {

    private final String queueName;
    private final ScheduledExecutorService scheduledExecutorService;
    private final BlockingQueue<Event> runnableBlockingQueue;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    /**
     * new 单线程执行器，因为队列事件，必然是单线程的
     *
     * @param threadPrefix 线程名称
     */
    public EventQueue(String queueName, String threadPrefix) {
        this(queueName, new BaseScheduledExecutor(threadPrefix, 1), 3000);
    }

    /**
     * new 单线程执行器，因为队列事件，必然是单线程的
     *
     * @param threadPrefix 线程名称
     * @param queueMaxSize 队列最大长度
     */
    public EventQueue(String queueName, String threadPrefix, int queueMaxSize) {
        this(queueName, new BaseScheduledExecutor(threadPrefix, 1), queueMaxSize);
    }

    /**
     * 指定执行器的队列
     *
     * @param queueName                队列名称
     * @param scheduledExecutorService 执行器
     */
    public EventQueue(String queueName, ScheduledExecutorService scheduledExecutorService) {
        this(queueName, scheduledExecutorService, 3000);
    }

    /**
     * 指定执行器的队列
     *
     * @param queueName                队列名称
     * @param scheduledExecutorService 执行器
     * @param queueMaxSize             队列最大长度
     */
    public EventQueue(String queueName, ScheduledExecutorService scheduledExecutorService, int queueMaxSize) {
        this.queueName = queueName;
        this.scheduledExecutorService = scheduledExecutorService;
        this.runnableBlockingQueue = new ArrayBlockingQueue<>(queueMaxSize);
        ExecutorService.put(queueName, this);
    }

    Event curPoll = null;

    @Override protected void onEvent() throws Throwable {
        try {
            curPoll = runnableBlockingQueue.poll();
            if (curPoll != null) {
                GuardThread.ins.push(curPoll);
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
                this.scheduledExecutorService.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override public void execute(Runnable command) {
        Event event = RunEvent.of(5, command);
        event.queueName = queueName;
        runnableBlockingQueue.add(event);
        try {
            reentrantLock.lock();
            if (atomicBoolean.compareAndSet(false, true)) {
                this.scheduledExecutorService.execute(this);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public <T> CompletableFuture<T> submit(Callable<T> task) {
        EventCallable<T> eventCallable = new EventCallable<>(4) {
            @Override public T call() throws Exception {
                return task.call();
            }
        };
        execute(eventCallable);
        return eventCallable.future;
    }

    public <T> CompletableFuture<T> submit(EventCallable<T> task) {
        execute(task);
        return task.future;
    }

    /** 延迟任务 */
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        Event scheduledEvent = createScheduledEvent(command);
        return scheduledExecutorService.schedule(scheduledEvent, delay, unit);
    }

    /** 间隔执行任务，无限期循环，当上一次没有执行完成 依然会执行下一次，只跟间隔时间有关系 */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        Event scheduledEvent = createScheduledEvent(command);
        return scheduledExecutorService.scheduleAtFixedRate(scheduledEvent, initialDelay, period, unit);
    }

    /** 间隔执行任务，无限期循环，当上一次没有执行完成不会执行下一次 */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        Event scheduledEvent = createScheduledEvent(command);
        return scheduledExecutorService.scheduleWithFixedDelay(scheduledEvent, initialDelay, delay, unit);
    }

    Event createScheduledEvent(Runnable runnable) {
        Event event = RunEvent.of(6, runnable);
        return new Event(5) {
            @Override protected void onEvent() throws Throwable {
                EventQueue.this.execute(event);
            }
        };
    }

}
