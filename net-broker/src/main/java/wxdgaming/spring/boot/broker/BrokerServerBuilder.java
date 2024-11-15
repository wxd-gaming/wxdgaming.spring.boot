package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.MessageDispatcher;
import wxdgaming.spring.boot.net.SessionGroup;
import wxdgaming.spring.boot.net.SessionHandler;
import wxdgaming.spring.boot.net.server.ServerMessageEncode;
import wxdgaming.spring.boot.net.server.SocketServerBuilder;

import java.lang.reflect.Constructor;

/**
 * socket 服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-06 19:48
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
public class BrokerServerBuilder {

    @Value("${socket.server.broker}")
    private SocketServerBuilder.Config broker;
    private final SessionGroup sessionGroup = new SessionGroup();

    @Bean(name = "brokerMessageDecode")
    @ConditionalOnMissingBean(BrokerMessageDecode.class)
    public BrokerMessageDecode brokerMessageDecode(BootstrapBuilder bootstrapBuilder, MessageDispatcher messageDispatcher) {
        return new BrokerMessageDecode(bootstrapBuilder, messageDispatcher, sessionGroup);
    }

    @Bean(name = "brokerService")
    @ConditionalOnProperty(prefix = "socket.server.broker", name = "port")
    public BrokerService brokerService(BootstrapBuilder bootstrapBuilder,
                                       SocketServerBuilder socketServerBuilder,
                                       SessionHandler sessionHandler,
                                       BrokerMessageDecode brokerMessageDecode,
                                       ServerMessageEncode serverMessageEncode) throws Exception {

        if (StringsUtil.emptyOrNull(broker.getServiceClass())) {
            broker.setServiceClass(BrokerService.class.getName());
        }

        Class aClass = Thread.currentThread().getContextClassLoader().loadClass(broker.getServiceClass());
        Constructor<BrokerService> declaredConstructor = aClass.getDeclaredConstructors()[0];
        return declaredConstructor.newInstance(
                bootstrapBuilder,
                socketServerBuilder,
                broker,
                sessionHandler,
                sessionGroup,
                brokerMessageDecode,
                serverMessageEncode
        );

    }

}
