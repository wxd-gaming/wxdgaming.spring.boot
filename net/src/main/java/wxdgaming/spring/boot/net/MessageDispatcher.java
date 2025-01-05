package wxdgaming.spring.boot.net;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.SpringReflectContent;
import wxdgaming.spring.boot.core.system.AnnUtil;
import wxdgaming.spring.boot.core.threading.BaseScheduledExecutor;
import wxdgaming.spring.boot.core.threading.Event;
import wxdgaming.spring.boot.core.threading.ExecutorWith;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.message.PojoBase;
import wxdgaming.spring.boot.net.message.SerializerUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
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
    protected final ConcurrentHashMap<Integer, Class<? extends PojoBase>> messageId2Class = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Integer> messageName2Id = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Class<? extends PojoBase>> messageName2Class = new ConcurrentHashMap<>();

    private boolean printLogger = false;

    @Setter MessageDispatcherHandler dispatcherHandler;

    public MessageDispatcher(boolean printLogger) {
        this.printLogger = printLogger;
        dispatcherHandler = new MessageDispatcherHandler(printLogger) {};
    }

    public void initMapping(SpringReflectContent springReflectContent, String[] packages) {
        Predicate<Class<?>> filter = clazz -> {
            if (packages == null || packages.length == 0) return true;
            for (String p : packages) {
                if (clazz.getName().startsWith(p)) return true;
            }
            return false;
        };
        springReflectContent.withMethodAnnotated(ProtoMapper.class)
                .filter(t -> filter.test(t.getLeft().getClass()))
                .forEach(t -> {
                    Class parameterType = t.getRight().getParameterTypes()[1];
                    if (PojoBase.class.isAssignableFrom(parameterType)) {
                        ProtoMapper protoMapper = AnnUtil.ann(t.getRight(), ProtoMapper.class);
                        ExecutorWith executorWith = AnnUtil.ann(t.getRight(), ExecutorWith.class);
                        DoMessageMapping messageMapping = new DoMessageMapping(protoMapper, executorWith, t.getLeft(), t.getRight(), parameterType);
                        int msgId = registerMessage(parameterType);
                        mappings.put(msgId, messageMapping);
                        log.debug("扫描消息处理接口 {}#{} {}", t.getLeft().getClass().getName(), t.getRight().getName(), parameterType.getName());
                    }
                });
    }

    /**
     * 解码消息
     *
     * @param msgId        消息id
     * @param messageBytes 消息报文
     * @param <T>          消息体类型
     * @return 消息体
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2025-01-05 11:04
     */
    public <T extends PojoBase> T decode(int msgId, byte[] messageBytes) {
        Class<? extends PojoBase> aClass = messageId2Class.get(msgId);
        return (T) SerializerUtil.decode(messageBytes, aClass);
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
        messageName2Class.put(pojoClass.getName(), pojoClass);
        messageId2Class.put(msgId, pojoClass);
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
            executor(socketSession, doMessageMapping.getExecutor(), doMessageMapping.queueName(), event);
        } else {
            dispatcherHandler.msgBytesNotDispatcher(socketSession, msgId, messageBytes);
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
            executor(socketSession, doMessageMapping.getExecutor(), doMessageMapping.queueName(), event);
        } else {
            dispatcherHandler.msgNotDispatcher(socketSession, msgId, message);
        }
    }

    protected void executor(SocketSession socketSession, Executor executor, String queueName, Event event) {
        if (StringUtils.isBlank(queueName)) {
            executor.execute(event);
        } else if (executor instanceof BaseScheduledExecutor scheduledExecutor) {
            scheduledExecutor.execute(queueName, event);
        } else {
            throw new UnsupportedOperationException(executor.getClass().getName() + " - 无法执行队列任务");
        }
    }
}
