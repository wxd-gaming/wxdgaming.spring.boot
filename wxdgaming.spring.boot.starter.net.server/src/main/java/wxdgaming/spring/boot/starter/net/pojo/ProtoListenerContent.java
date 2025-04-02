package wxdgaming.spring.boot.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.SpringReflectContent;
import wxdgaming.spring.boot.starter.core.io.Objects;
import wxdgaming.spring.boot.starter.core.system.AnnUtil;
import wxdgaming.spring.boot.starter.core.util.StringUtils;
import wxdgaming.spring.boot.starter.net.ann.ProtoRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * proto容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 08:39
 **/
@Slf4j
@Getter
public class ProtoListenerContent {

    private final ConcurrentHashMap<Integer, ProtoMapping> mappingMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Class<? extends PojoBase>> messageId2MappingMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends PojoBase>, Integer> message2MappingMap = new ConcurrentHashMap<>();

    final SpringReflectContent runApplication;

    public ProtoListenerContent(SpringReflectContent runApplication) {
        this.runApplication = runApplication;
        runApplication
                .withMethodAnnotated(ProtoRequest.class)
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getIns();
                    Method method = contentMethod.getMethod();

                    ProtoRequest methodRequestMapping = AnnUtil.ann(method, ProtoRequest.class);

                    Class<? extends PojoBase> pojoClass = findPojoClass(method);
                    if (pojoClass == null) {
                        throw new RuntimeException("未找到消息类: %s".formatted(method));
                    }
                    int messageId = messageId(pojoClass);

                    ProtoMapping mapping = new ProtoMapping(methodRequestMapping, messageId, pojoClass, ins, method);

                    ProtoMapping old = mappingMap.put(messageId, mapping);
                    if (old != null && !Objects.equals(old.ins().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s, old = %s - new = %s"
                                .formatted(
                                        messageId,
                                        old.ins().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("proto listener messageId: {} handler: {}", messageId, ins.getClass());
                });
    }

    public int register(Class<? extends PojoBase> pojoClass) {
        int hashcode = StringUtils.hashcode(pojoClass.getName());
        message2MappingMap.put(pojoClass, hashcode);
        Class<? extends PojoBase> old = messageId2MappingMap.putIfAbsent(hashcode, pojoClass);
        if (old != null && !Objects.equals(old.getName(), pojoClass.getName())) {
            throw new RuntimeException("重复注册消息id: %s %s".formatted(hashcode, pojoClass));
        }
        return hashcode;
    }

    public int messageId(Class<? extends PojoBase> pojoClass) {
        Integer hashcode = message2MappingMap.get(pojoClass);
        if (hashcode == null) {
            return register(pojoClass);
        }
        return hashcode;
    }

    public Class<? extends PojoBase> message(int messageId) {
        Class<? extends PojoBase> hashcode = messageId2MappingMap.get(messageId);
        if (hashcode == null) {
            throw new RuntimeException("未注册消息id: %s".formatted(messageId));
        }
        return hashcode;
    }

    public Class<? extends PojoBase> findPojoClass(Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            if (type instanceof Class<?> clazz) {
                if (PojoBase.class.isAssignableFrom(clazz)) {
                    return (Class<? extends PojoBase>) clazz;
                }
            }
        }
        return null;
    }

}
