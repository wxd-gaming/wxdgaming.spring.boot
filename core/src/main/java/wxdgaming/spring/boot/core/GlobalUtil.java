package wxdgaming.spring.boot.core;


import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import wxdgaming.spring.boot.core.function.Consumer2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全局处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-11 16:52
 **/
public class GlobalUtil {

    /** 当前服务器的debug状态 */
    public static final AtomicBoolean DEBUG = new AtomicBoolean();
    /** 停服关闭状态 */
    public static final AtomicBoolean SHUTTING = new AtomicBoolean();

    private static List<Consumer2<Object, Throwable>> exceptionCall = new ArrayList<>();

    public static void register(Consumer2<Object, Throwable> consumer) {
        synchronized (GlobalUtil.class) {
            List<Consumer2<Object, Throwable>> tmp = new ArrayList<>(exceptionCall);
            tmp.add(consumer);
            exceptionCall = tmp;
        }
    }

    public static void remove(Consumer2<Object, Throwable> consumer) {
        synchronized (GlobalUtil.class) {
            List<Consumer2<Object, Throwable>> tmp = new ArrayList<>(exceptionCall);
            tmp.remove(consumer);
            exceptionCall = tmp;
        }
    }

    public static void exception(Object msg, Object... params) {
        FormattingTuple formatter = MessageFormatter.arrayFormat(msg.toString(), params);
        String message = formatter.getMessage();
        LogbackUtil.logger(3).error("{}", message, formatter.getThrowable());
        for (Consumer2<Object, Throwable> consumer : exceptionCall) {
            consumer.accept(message, formatter.getThrowable());
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> SHUTTING.set(true)));
    }
}
