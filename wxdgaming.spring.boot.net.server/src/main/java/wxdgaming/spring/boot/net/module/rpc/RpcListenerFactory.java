package wxdgaming.spring.boot.net.module.rpc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.Init;

/**
 * rpc 监听 绑定工厂
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 16:36
 **/
@Slf4j
@Getter
@Component
public class RpcListenerFactory {

    /** 相当于用 read and copy write方式作为线程安全性 */
    RpcListenerContent rpcListenerContent = null;

    @Init
    @Order(9)
    public void init(ApplicationContextProvider runApplication) {
        rpcListenerContent = new RpcListenerContent(runApplication);
    }

}
