package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.ReLoad;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.DoMessageMapping;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.message.PojoBase;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息派发服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-17 11:35
 **/
@Slf4j
@Getter
public class BrokerMessageDispatcher implements InitPrint {

    protected final ConcurrentHashMap<Integer, DoMessageMapping> mappings = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, Integer> messageName2Id = new ConcurrentHashMap<>();

    @Start
    @ReLoad
    @Order(999)
    public void initMapping(SpringUtil springUtil) {
        springUtil.withMethodAnnotated(MsgMapper.class)
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

        int msgId = StringsUtil.hashcode(pojoClass.getName());
        if (messageName2Id.put(pojoClass.getName(), msgId) == null) {
            log.debug("扫描注册消息：{} = {}", pojoClass.getName(), msgId);
        }

        return msgId;
    }

}
