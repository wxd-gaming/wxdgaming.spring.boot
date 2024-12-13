package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.SessionGroup;
import wxdgaming.spring.boot.net.server.ServerConfig;
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
@ConfigurationProperties("socket.broker")
@ConditionalOnProperty(prefix = "socket.broker.config", name = "port")
public class BrokerServerBuilder {

    private ServerConfig config;
    private final SessionGroup sessionGroup = new SessionGroup();

    @Bean(name = "brokerMessageDecode")
    @ConditionalOnMissingBean(BrokerMessageDispatcher.class)
    public BrokerMessageDispatcher brokerMessageDispatcher() {
        return new BrokerMessageDispatcher(config.getScanPkgs());
    }

    @Bean(name = "brokerMessageDecode")
    @ConditionalOnMissingBean(name = "brokerMessageDecode")
    public BrokerMessageDecode brokerMessageDecode(BootstrapBuilder bootstrapBuilder, BrokerMessageDispatcher serverMessageDispatcher, DataCenter dataCenter) {
        return new BrokerMessageDecode(bootstrapBuilder, serverMessageDispatcher, sessionGroup, dataCenter);
    }

    @Bean(name = "brokerMessageDecode")
    @ConditionalOnMissingBean(name = "brokerMessageEncode")
    public BrokerMessageEncode brokerMessageEncode(BrokerMessageDispatcher serverMessageDispatcher) {
        return new BrokerMessageEncode(serverMessageDispatcher);
    }

    @Bean(name = "brokerService")
    public BrokerService brokerService(BootstrapBuilder bootstrapBuilder,
                                       SocketServerBuilder socketServerBuilder,
                                       BrokerMessageDecode brokerMessageDecode,
                                       BrokerMessageEncode brokerMessageEncode) throws Exception {

        if (StringsUtil.emptyOrNull(config.getServiceClass())) {
            config.setServiceClass(BrokerService.class.getName());
        }

        Class aClass = Thread.currentThread().getContextClassLoader().loadClass(config.getServiceClass());
        Constructor<BrokerService> declaredConstructor = aClass.getDeclaredConstructors()[0];
        BrokerService brokerService = declaredConstructor.newInstance(
                bootstrapBuilder,
                socketServerBuilder,
                config,
                brokerMessageDecode,
                brokerMessageEncode
        );
        brokerService.setSessionGroup(sessionGroup);
        return brokerService;

    }

}
