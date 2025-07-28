package wxdgaming.spring.boot.core.executor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.reflect.AnnUtil;

import java.lang.reflect.Method;

/**
 * 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-15 19:56
 **/
@Slf4j
public abstract class ExecutorEvent extends ExecutorJob implements IExecutorQueue {

    protected ExecutorWith executorWith;
    @Setter protected String queueName;

    public ExecutorEvent() {
        super(null);
    }

    public ExecutorEvent(Method method) {
        super(null);
        this.executorWith = AnnUtil.ann(method, ExecutorWith.class);
        this.queueName = this.executorWith == null ? null : this.executorWith.queueName();
    }

    @Override public String getStack() {
        return super.getStack();
    }

    @Override public String queueName() {
        return queueName;
    }

    @Override public void run() {
        try {
            ExecutorMonitor.put(this);
            if (this.getThreadContext() != null) {
                ThreadContext.context().putAllIfAbsent(this.getThreadContext());
            }
            onEvent();
        } catch (Throwable throwable) {
            log.error("{}", getStack(), throwable);
        } finally {
            this.threadContext = null;
            ThreadContext.cleanup();
            ExecutorMonitor.release();
            runAfter();
        }
    }

    public abstract void onEvent() throws Exception;

    public void submit() {
        ExecutorService executorService = ExecutorFactory.getExecutorServiceLogic();
        if (executorWith != null) {
            String threadName = executorWith.threadName();
            if (Utils.isNotBlank(threadName)) {
                executorService = ExecutorFactory.getExecutor(threadName);
            }else if(executorWith.useVirtualThread()){
                executorService = ExecutorFactory.getExecutorServiceVirtual();
            }
        }
        executorService.execute(this);
    }

}
