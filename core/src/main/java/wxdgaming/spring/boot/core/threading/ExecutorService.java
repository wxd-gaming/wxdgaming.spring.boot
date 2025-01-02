package wxdgaming.spring.boot.core.threading;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.InitPrint;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 13:57
 **/
@Getter
@Setter
public class ExecutorService implements InitPrint {

    static ConcurrentHashMap<String, Executor> executorMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, EventQueue> eventQueueMap = new ConcurrentHashMap<>();

    public static void put(String name, Executor executor) {
        if (executorMap.put(name, executor) != null) {
            throw new RuntimeException("不允许出现相同名称的bean=" + name);
        }
    }

    public static void put(String name, EventQueue eventQueue) {
        if (eventQueueMap.put(name, eventQueue) != null) {
            throw new RuntimeException("不允许出现相同名称的bean=" + name);
        }
    }

    public static <T extends Executor> T getExecutor(String name) {
        return (T) executorMap.get(name);
    }

    public static <T extends EventQueue> T getEventQueue(String name) {
        return (T) eventQueueMap.get(name);
    }

    public static <T extends EventQueue> T getEventQueueOrNew(String name, ScheduledExecutorService executor) {
        return (T) eventQueueMap.computeIfAbsent(name, q -> new EventQueue(name, executor));
    }

    public static DefaultExecutor getDefaultExecutor() {
        return (DefaultExecutor) executorMap.get("default");
    }

    public static LogicExecutor getLogicExecutor() {
        return (LogicExecutor) executorMap.get("logic");
    }

    public static VirtualExecutor getVirtualExecutor() {
        return VirtualExecutor.getIns();
    }

}
