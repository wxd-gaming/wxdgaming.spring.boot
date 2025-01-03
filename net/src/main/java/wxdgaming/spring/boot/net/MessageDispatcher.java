package wxdgaming.spring.boot.net;

import ch.qos.logback.core.LogbackUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.SpringReflectContent;
import wxdgaming.spring.boot.core.function.Consumer2;
import wxdgaming.spring.boot.core.function.Consumer3;
import wxdgaming.spring.boot.core.system.AnnUtil;
import wxdgaming.spring.boot.core.threading.Event;
import wxdgaming.spring.boot.core.threading.ExecutorWith;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.message.PojoBase;
import wxdgaming.spring.boot.net.message.SerializerUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 消息派发服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-17 11:35
 **/
@Slf4j
@Getter
public abstract class MessageDispatcher implements InitPrint {

    protected final ConcurrentHashMap<Integer, DoMessageMapping> mappings = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Integer> messageName2Id = new ConcurrentHashMap<>();

    private boolean printLogger = false;
    private final String[] packages;
    @Setter private Consumer3<SocketSession, Integer, byte[]> msgBytesNotDispatcher = (session, msgId, messageBytes) -> {
        if (printLogger) {
            Logger logger = LogbackUtil.logger();
            logger.debug(
                    "收到消息：ctx={}, id={}, len={} (未知消息)",
                    session.toString(),
                    msgId,
                    messageBytes.length
            );
        }
    };

    @Setter private Consumer3<SocketSession, Integer, PojoBase> msgNotDispatcher = (session, msgId, message) -> {
        if (printLogger) {
            Logger logger = LogbackUtil.logger();
            logger.debug(
                    "收到消息：ctx={}, id={}, mes={}",
                    session.toString(),
                    msgId,
                    message.toString()
            );
        }
    };

    @Setter private Consumer2<SocketSession, String> stringDispatcher = (session, message) -> {
        if (printLogger) {
            Logger logger = LogbackUtil.logger();
            logger.debug(
                    "收到消息：ctx={}, mes={}",
                    session.toString(),
                    message
            );
        }
    };

    public MessageDispatcher(boolean printLogger, String[] packages) {
        this.printLogger = printLogger;
        this.packages = packages;
    }

    public void initMapping(SpringReflectContent springReflectContent) {
        initMapping(springReflectContent, packages);
    }

    public void initMapping(SpringReflectContent springReflectContent, String[] params) {
        Predicate<Class<?>> filter = clazz -> {
            if (params == null || params.length == 0) return true;
            for (String p : params) {
                if (clazz.getName().startsWith(p)) return true;
            }
            return false;
        };
        springReflectContent.withMethodAnnotated(MsgMapper.class)
                .filter(t -> filter.test(t.getLeft().getClass()))
                .forEach(t -> {
                    Class parameterType = t.getRight().getParameterTypes()[1];
                    if (PojoBase.class.isAssignableFrom(parameterType)) {
                        MsgMapper msgMapper = AnnUtil.ann(t.getRight(), MsgMapper.class);
                        ExecutorWith executorWith = AnnUtil.ann(t.getRight(), ExecutorWith.class);
                        DoMessageMapping messageMapping = new DoMessageMapping(msgMapper, executorWith, t.getLeft(), t.getRight(), parameterType);
                        int msgId = registerMessage(parameterType);
                        mappings.put(msgId, messageMapping);
                        log.debug("扫描消息处理接口 {}#{} {}", t.getLeft().getClass().getName(), t.getRight().getName(), parameterType.getName());
                    }
                });
    }

    public void registerMessage(ClassLoader classLoader, String... packages) {
        ReflectContext build = ReflectContext.Builder.of(classLoader, packages).build();
        registerMessage(build);
    }

    public void registerMessage(ReflectContext reflectContext) {
        reflectContext
                .withSuper(PojoBase.class)
                .map(v -> (Class) v.getCls())
                .forEach(this::registerMessage);
    }

    public int registerMessage(Class<? extends PojoBase> pojoClass) {

        int msgId = getMessage(pojoClass);
        if (messageName2Id.put(pojoClass.getName(), msgId) == null) {
            log.debug("扫描注册消息：{} = {}", pojoClass.getName(), msgId);
        }

        return msgId;
    }

    public int getMessage(Class<? extends PojoBase> pojoClass) {
        return StringsUtil.hashcode(pojoClass.getName());
    }

    public void dispatch(SocketSession socketSession, int msgId, byte[] messageBytes) throws Exception {
        DoMessageMapping doMessageMapping = getMappings().get(msgId);
        if (doMessageMapping != null) {
            Event event = new Event() {
                @Override protected void onEvent() throws Throwable {
                    PojoBase message = (PojoBase) SerializerUtil.decode(messageBytes, doMessageMapping.messageType());
                    /* TODO 这里考虑如何线程规划 */
                    doMessageMapping.method().invoke(doMessageMapping.bean(), socketSession, message);
                }
            };
            doMessageMapping.executor(event);
        } else {
            msgBytesNotDispatcher.accept(socketSession, msgId, messageBytes);
        }
    }

    public void dispatch(SocketSession socketSession, PojoBase message) throws Exception {
        int msgId = getMessage(message.getClass());
        dispatch(socketSession, msgId, message);
    }

    public void dispatch(SocketSession socketSession, int msgId, PojoBase message) throws Exception {
        DoMessageMapping doMessageMapping = getMappings().get(msgId);
        if (doMessageMapping != null) {
            Event event = new Event() {
                @Override protected void onEvent() throws Throwable {
                    /* TODO 这里考虑如何线程规划 */
                    doMessageMapping.method().invoke(doMessageMapping.bean(), socketSession, message);
                }
            };
            doMessageMapping.executor(event);
        } else {
            msgNotDispatcher.accept(socketSession, msgId, message);
        }
    }

}
