package wxdgaming.spring.boot.net;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.message.PojoBase;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息派发服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-17 11:35
 **/
@Slf4j
@Getter
public class MessageDispatcher implements InitPrint {

    private final ConcurrentHashMap<Integer, DoMessageMapping> mappings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> messageName2Id = new ConcurrentHashMap<>();

    @Start
    public void start(SpringUtil springUtil) {
        springUtil.withMethodAnnotated(MsgMapper.class)
                .forEach(method -> {
                    Class parameterType = method.getParameterTypes()[1];
                    if (PojoBase.class.isAssignableFrom(parameterType)) {
                        DoMessageMapping messageMapping = new DoMessageMapping(springUtil.getBean(method.getDeclaringClass()), method, parameterType);
                        int msgId = registerMessage(parameterType);
                        mappings.put(msgId, messageMapping);
                        log.debug("扫描消息处理接口 {}#{} {}", method.getDeclaringClass().getName(), method.getName(), parameterType.getName());
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
                .forEach(pojo -> {registerMessage(pojo);});
    }

    public int registerMessage(Class<? extends PojoBase> pojoClass) {

        int msgId = StringsUtil.hashcode(pojoClass.getName());
        if (messageName2Id.put(pojoClass.getName(), msgId) == null) {
            log.debug("扫描注册消息：{} = {}", pojoClass.getName(), msgId);
        }

        return msgId;
    }

}
