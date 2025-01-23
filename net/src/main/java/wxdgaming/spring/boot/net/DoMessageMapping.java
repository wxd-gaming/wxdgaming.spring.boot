package wxdgaming.spring.boot.net;

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
public record DoMessageMapping(ProtoMapping mapper, ExecutorWith executorWith, Object bean, Method method,
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

    public String queueName() {
        String queueName = null;
        if (executorWith() != null) {
            queueName = executorWith().queueName();
        }
        return queueName;
    }

}
