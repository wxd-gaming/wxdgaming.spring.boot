package wxdgaming.spring.boot.net.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.BootstrapBuilder;

/**
 * server 1
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-12 20:12
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.server1")
@ConditionalOnProperty(prefix = "socket.server1.config", name = "port")
public class SocketServer1Builder implements InitPrint {

    ServerConfig config;

    @Primary
    @Bean(name = "serverMessageDispatcher1")
    @ConditionalOnMissingBean(name = "serverMessageDispatcher1")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDispatcher serverMessageDispatcher() {
        ServerMessageDispatcher messageDispatcher = new ServerMessageDispatcher(config.getScanPkgs());
        log.debug("init default serverDispatcher1 = {}", messageDispatcher.hashCode());
        return messageDispatcher;
    }

    @Primary
    @Bean(name = "serverMessageEncode1")
    @ConditionalOnMissingBean(name = "serverMessageEncode1")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageEncode serverMessageEncode(@Qualifier("serverMessageDispatcher1") ServerMessageDispatcher messageDispatcher) {
        ServerMessageEncode encode = new ServerMessageEncode(messageDispatcher) {};
        log.debug("init default ServerMessageEncode1 = {}", encode.hashCode());
        return encode;
    }

    @Primary
    @Bean(name = "serverMessageDecode1")
    @ConditionalOnMissingBean(name = "serverMessageDecode1")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDecode serverMessageDecode(BootstrapBuilder bootstrapBuilder,
                                                   @Qualifier("serverMessageDispatcher1") ServerMessageDispatcher messageDispatcher) {
        ServerMessageDecode decode = new ServerMessageDecode(bootstrapBuilder, messageDispatcher) {};
        log.debug("init default ServerMessageDecode1 = {}", decode.hashCode());
        return decode;
    }

    @Bean(name = "socketService1")
    public SocketService socketService1(BootstrapBuilder bootstrapBuilder,
                                        @Qualifier("serverMessageDecode1") ServerMessageDecode serverMessageDecode,
                                        @Qualifier("serverMessageEncode1") ServerMessageEncode serverMessageEncode) throws Exception {

        return BootstrapBuilder.createSocketService(bootstrapBuilder, serverMessageDecode, serverMessageEncode, config);
    }

}
