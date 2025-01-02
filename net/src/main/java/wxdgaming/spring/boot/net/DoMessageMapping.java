package wxdgaming.spring.boot.net;

import org.apache.commons.lang3.StringUtils;
import wxdgaming.spring.boot.core.threading.BaseScheduledExecutor;
import wxdgaming.spring.boot.core.threading.ExecutorService;
import wxdgaming.spring.boot.core.threading.ExecutorWith;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * 处理映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-19 19:51
 **/
public record DoMessageMapping(MsgMapper mapper, ExecutorWith executorWith, Object bean, Method method,
        Class<?> messageType) {
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
