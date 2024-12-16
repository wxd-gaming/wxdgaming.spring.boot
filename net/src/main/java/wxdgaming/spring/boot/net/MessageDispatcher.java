package wxdgaming.spring.boot.net;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.SpringUtil;
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

    private final String[] packages;

    public MessageDispatcher(String[] packages) {
        this.packages = packages;
    }

    public void initMapping(SpringUtil springUtil) {
        initMapping(springUtil, packages);
    }

    public void initMapping(SpringUtil springUtil, String[] params) {
        Predicate<Class<?>> filter = clazz -> {
            if (params == null || params.length == 0) return true;
            if (clazz.getPackageName().startsWith(NetScan.class.getPackageName())) return true;
            for (String p : params) {
                if (clazz.getName().startsWith(p)) return true;
            }
            return false;
        };
        springUtil.withMethodAnnotated(MsgMapper.class)
                .filter(t -> filter.test(t.getLeft().getClass()))
                .forEach(t -> {
                    Class parameterType = t.getRight().getParameterTypes()[1];
                    if (PojoBase.class.isAssignableFrom(parameterType)) {
                        DoMessageMapping messageMapping = new DoMessageMapping(t.getLeft(), t.getRight(), parameterType);
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

    public boolean dispatch(SocketSession socketSession, int msgId, byte[] messageBytes) throws Exception {
        DoMessageMapping doMessageMapping = getMappings().get(msgId);
        if (doMessageMapping != null) {
            PojoBase message = (PojoBase) SerializerUtil.decode(messageBytes, doMessageMapping.getMessageType());
            /* TODO 这里考虑如何线程规划 */
            doMessageMapping.getMethod().invoke(doMessageMapping.getBean(), socketSession, message);
            return true;
        }
        return false;
    }

    public boolean dispatch(SocketSession socketSession, PojoBase message) throws Exception {
        int msgId = getMessage(message.getClass());
        return dispatch(socketSession, msgId, message);
    }

    public boolean dispatch(SocketSession socketSession, int msgId, PojoBase message) throws Exception {
        DoMessageMapping doMessageMapping = getMappings().get(msgId);
        if (doMessageMapping != null) {
            /* TODO 这里考虑如何线程规划 */
            doMessageMapping.getMethod().invoke(doMessageMapping.getBean(), socketSession, message);
            return true;
        }
        return false;
    }

}
