package wxdgaming.spring.boot.starter.net.module.inner;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.spring.boot.starter.core.SpringReflect;
import wxdgaming.spring.boot.starter.core.io.Objects;
import wxdgaming.spring.boot.starter.core.system.AnnUtil;
import wxdgaming.spring.boot.starter.core.util.StringUtils;
import wxdgaming.spring.boot.starter.net.ann.RpcRequest;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * rpc 监听 绑定工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:36
 **/
@Slf4j
@Getter
public class RpcListenerContent {

    final SpringReflect runApplication;
    final HashMap<String, RpcMapping> rpcMappingMap = new HashMap<>();

    public RpcListenerContent(SpringReflect runApplication) {
        this.runApplication = runApplication;
        this.runApplication.getSpringReflectContent()
                .withMethodAnnotated(RpcRequest.class)
                .forEach(contentMethod -> {
                    Object ins = contentMethod.getIns();
                    Method method = contentMethod.getMethod();

                    RequestMapping insRequestMapping = AnnUtil.ann(ins.getClass(), RequestMapping.class);
                    RpcRequest methodRequestMapping = AnnUtil.ann(method, RpcRequest.class);

                    String path = "";

                    if (insRequestMapping != null) {
                        if (insRequestMapping.value().length > 0 && StringUtils.isNotBlank(insRequestMapping.value()[0])) {
                            path += insRequestMapping.value()[0];
                        } else if (insRequestMapping.path().length > 0 && StringUtils.isNotBlank(insRequestMapping.path()[0])) {
                            path += insRequestMapping.path()[0];
                        }
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
                    RpcMapping rpcMapping = new RpcMapping(methodRequestMapping, lowerCase, ins, method);

                    RpcMapping old = rpcMappingMap.put(lowerCase, rpcMapping);
                    if (old != null && !Objects.equals(old.ins().getClass().getName(), ins.getClass().getName())) {
                        String formatted = "重复路由监听 %s old = %s - new = %s"
                                .formatted(
                                        lowerCase,
                                        old.ins().getClass().getName(),
                                        ins.getClass().getName()
                                );
                        throw new RuntimeException(formatted);
                    }
                    log.debug("rpc listener url: {}", lowerCase);
                });
    }

}
