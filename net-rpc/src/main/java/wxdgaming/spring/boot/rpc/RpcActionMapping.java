package wxdgaming.spring.boot.rpc;


import org.apache.commons.lang3.StringUtils;
import wxdgaming.spring.boot.core.threading.BaseScheduledExecutor;
import wxdgaming.spring.boot.core.threading.ExecutorService;
import wxdgaming.spring.boot.core.threading.ExecutorWith;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

record RpcActionMapping(RPC annotation, ExecutorWith executorWith, Object bean, Method method) {

    public Executor getExecutor() {
        String executorName = null;
        if (executorWith() != null) {
            executorName = executorWith().threadName();
        }
        if (StringsUtil.emptyOrNull(executorName)) {
            executorName = "logic";
        }
        Executor executor = ExecutorService.getExecutor(executorName);
        if (executor == null) {
            throw new UnsupportedOperationException("threadName=" + executorName + " 查找失败");
        }
        return executor;
    }

    public void executor(Runnable task) {
        Executor executor = getExecutor();
        String queueName = null;
        if (executorWith() != null) {
            queueName = executorWith().queueName();
        }
        if (StringUtils.isBlank(queueName)) {
            executor.execute(task);
        } else if (executor instanceof BaseScheduledExecutor scheduledExecutor) {
            scheduledExecutor.execute(queueName, task);
        } else {
            throw new UnsupportedOperationException(executor.getClass().getName() + " - 无法执行队列任务");
        }
    }

}
