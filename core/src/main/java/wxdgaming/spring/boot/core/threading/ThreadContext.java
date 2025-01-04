package wxdgaming.spring.boot.core.threading;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 本地线程变量
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-04-24 20:26
 **/
@Slf4j
@Getter
public class ThreadContext extends JSONObject {

    private static final ThreadLocal<ThreadContext> local = new ThreadLocal<>();

    /** 获取当前上下文 */
    public static ThreadContext context() {
        ThreadContext threadContext = local.get();
        if (threadContext == null) {
            threadContext = new ThreadContext();
            local.set(threadContext);
        }
        return threadContext;
    }

    public static void setQueueName(String queueName) {
        context().put("queueName", queueName);
    }

    public static String queueName() {
        ThreadContext context = contextOrNull();
        if (context == null) return "";
        return context.getString("queueName");
    }

    /** 获取当前上下文，如果尚未设置 null */
    public static ThreadContext contextOrNull() {
        return local.get();
    }

    /** 获取参数 */
    public static <T> T context(final Class<T> clazz) {
        return context(clazz, null);
    }

    public static <T> T context(final Class<T> clazz, T defaultValue) {
        ThreadContext context = contextOrNull();
        if (context == null) return defaultValue;
        return clazz.cast(context.get(clazz.getName()));
    }

    /** 获取参数 */
    public static <T> T context(final String name) {
        return context(name, null);
    }

    public static <T> T context(final String name, T defaultValue) {
        ThreadContext context = contextOrNull();
        if (context == null) return defaultValue;
        return (T) context.get(name);
    }

    /** put参数 */
    public static <T> T putContent(final Class<T> clazz) {
        try {
            T ins = clazz.getDeclaredConstructor().newInstance();
            putContentIfAbsent(ins);
            return ins;
        } catch (Exception e) {
            throw new RuntimeException(clazz.getName(), e);
        }
    }

    /** put参数 */
    public static <T> void putContent(final T ins) {
        context().put(ins.getClass().getName(), ins);
    }

    /** put参数 */
    public static <T> void putContent(final String name, T ins) {
        context().put(name, ins);
    }

    /** put参数 */
    public static <T> void putContentIfAbsent(final T ins) {
        context().putIfAbsent(ins.getClass().getName(), ins);
    }

    /** put参数 */
    public static <T> void putContentIfAbsent(final String name, T ins) {
        context().putIfAbsent(name, ins);
    }

    /** 设置参数 */
    public static void set(ThreadContext threadContext) {
        local.set(threadContext);
    }

    /** 清理缓存 */
    public static void cleanup() {
        local.remove();
    }

    /** 清理缓存 */
    public static void cleanup(Class<?> clazz) {
        context().remove(clazz.getName());
    }

    /** 清理缓存 */
    public static void cleanup(String name) {
        context().remove(name);
    }

    public ThreadContext() {
    }

    public ThreadContext(Map m) {
        super(m.size());
        putAll(m);
    }

}
