package wxdgaming.spring.boot.core.executor;

import lombok.Getter;
import wxdgaming.spring.boot.core.CoreProperties;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程执行器工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-15 11:20
 **/
public class ExecutorFactory {

    private static ExecutorMonitor EXECUTOR_MONITOR;
    @Getter private static ScheduledExecutorService scheduledExecutorService;
    @Getter private static ConcurrentHashMap<String, ExecutorService> EXECUTOR_MAP;

    @Getter private static ExecutorService EXECUTOR_SERVICE_BASIC;
    @Getter private static ExecutorService EXECUTOR_SERVICE_LOGIC;
    @Getter private static ExecutorService EXECUTOR_SERVICE_VIRTUAL;

    public static void init(CoreProperties coreProperties) {
        EXECUTOR_MAP = new ConcurrentHashMap<>();
        EXECUTOR_MONITOR = new ExecutorMonitor();
        scheduledExecutorService = newSingleThreadScheduledExecutor("scheduled");
        EXECUTOR_SERVICE_BASIC = create("basic", coreProperties.getBasic());
        EXECUTOR_SERVICE_LOGIC = create("logic", coreProperties.getLogic());
        EXECUTOR_SERVICE_VIRTUAL = createVirtual("virtual", coreProperties.getVirtual());
    }

    public static ExecutorService getExecutor(String name) {
        return getExecutorMap().get(name);
    }

    public static ScheduledExecutorService newSingleThreadScheduledExecutor(String name) {
        return Executors.newSingleThreadScheduledExecutor(new NameThreadFactory(name, true));
    }

    public static ExecutorServicePlatform create(String name, ExecutorConfig executorConfig) {
        return create(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    /**
     * 创建一个平台线程池，默认队列长度是5000，默认拒绝策略是AbortPolicy
     *
     * @param name         名
     * @param corePoolSize 核心线程数
     */
    public static ExecutorServicePlatform create(String name, int corePoolSize) {
        return create(name, corePoolSize, 5000, QueuePolicyConst.AbortPolicy);
    }

    public static ExecutorServicePlatform create(String name, int corePoolSize, int queueSize, QueuePolicy queuePolicy) {
        ExecutorServicePlatform executorServicePlatform = new ExecutorServicePlatform(name, corePoolSize, queueSize, queuePolicy);
        getExecutorMap().put(name, executorServicePlatform);
        return executorServicePlatform;
    }

    public static ExecutorServiceVirtual createVirtual(String name, ExecutorConfig executorConfig) {
        return createVirtual(name, executorConfig.getCoreSize(), executorConfig.getMaxQueueSize(), executorConfig.getQueuePolicy());
    }

    /**
     * 创建一个虚拟线程池，默认队列长度是5000，默认拒绝策略是AbortPolicy
     *
     * @param name         名
     * @param corePoolSize 核心线程数
     */
    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize) {
        return createVirtual(name, corePoolSize, 5000, QueuePolicyConst.AbortPolicy);
    }

    public static ExecutorServiceVirtual createVirtual(String name, int corePoolSize, int queueSize, QueuePolicy queuePolicy) {
        ExecutorServiceVirtual executorServiceVirtual = new ExecutorServiceVirtual(name, corePoolSize, queueSize, queuePolicy);
        getExecutorMap().put(name, executorServiceVirtual);
        return executorServiceVirtual;
    }

    public static ConcurrentHashMap<String, ExecutorService> getExecutorMap() {
        return EXECUTOR_MAP;
    }

    public static ExecutorService getExecutorServiceBasic() {
        return EXECUTOR_SERVICE_BASIC;
    }

    public static ExecutorService getExecutorServiceLogic() {
        return EXECUTOR_SERVICE_LOGIC;
    }

    public static ExecutorService getExecutorServiceVirtual() {
        return EXECUTOR_SERVICE_VIRTUAL;
    }
}
