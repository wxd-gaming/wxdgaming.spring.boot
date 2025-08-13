package wxdgaming.spring.boot.net.module.rpc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.assist.JavassistProxy;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.io.Objects;
import wxdgaming.spring.boot.core.reflect.AnnUtil;
import wxdgaming.spring.boot.net.ann.RpcRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * rpc 监听 绑定工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 16:36
 **/
@Slf4j
@Getter
public class RpcListenerContent {

    final ApplicationContextProvider contextProvider;
    final List<RpcFilter> rpcFilterList;
    final HashMap<String, RpcMapping> rpcMappingMap = new HashMap<>();

    public RpcListenerContent(ApplicationContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        this.rpcFilterList = contextProvider.classWithSuper(RpcFilter.class).toList();
        this.contextProvider.withMethodAnnotated(RpcRequest.class)
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getBean();
                    Method method = contentMethod.getMethod();

                    RequestMapping insRequestMapping = AnnUtil.ann(ins.getClass(), RequestMapping.class);
                    RpcRequest methodRequestMapping = AnnUtil.ann(method, RpcRequest.class);

                    String path = "";

                    if (insRequestMapping != null) {
                        path += insRequestMapping.path();
                    } else {
                        String simpleName = ins.getClass().getSimpleName();
                        if (simpleName.endsWith("Spi")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 3);
                        } else if (simpleName.endsWith("Impl")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 4);
                        } else if (simpleName.endsWith("Service")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 7);
                        } else if (simpleName.endsWith("Controller")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 10);
                        } else if (simpleName.endsWith("Api")) {
                            simpleName = simpleName.substring(0, simpleName.length() - 3);
                        }

                        path += simpleName + "/";
                    }
                    if (!path.startsWith("/")) path = "/" + path;
                    if (!path.endsWith("/")) path += "/";
                    if (StringUtils.isBlank(methodRequestMapping.path())) {
                        path += method.getName();
                    } else {
                        path += methodRequestMapping.path();
                    }

                    String lowerCase = path.toLowerCase();
                    JavassistProxy javassistProxy = JavassistProxy.of(ins, method);
                    RpcMapping rpcMapping = new RpcMapping(methodRequestMapping, lowerCase, javassistProxy);

                    RpcMapping old = rpcMappingMap.put(lowerCase, rpcMapping);
                    if (old != null && !Objects.equals(old.javassistProxy().getInstance().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s old = %s - new = %s"
                                .formatted(
                                        lowerCase,
                                        old.javassistProxy().getInstance().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("rpc listener url: {}", lowerCase);
                });
    }

}
