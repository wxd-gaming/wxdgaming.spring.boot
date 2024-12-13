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
import wxdgaming.spring.boot.net.BootstrapBuilder;

/**
 * socket 服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-06 19:48
 **/
@Setter
@Slf4j
@Getter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.server")
@ConditionalOnProperty(prefix = "socket.server.config", name = "port")
public class SocketServerBuilder {

    private ServerConfig config;

    @Primary
    @Bean(name = "serverMessageDispatcher")
    @ConditionalOnMissingBean(name = "serverMessageDispatcher")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDispatcher serverMessageDispatcher() {
        ServerMessageDispatcher messageDispatcher = new ServerMessageDispatcher(config.getScanPkgs());
        log.debug("init default serverMessageDispatcher = {}", messageDispatcher.hashCode());
        return messageDispatcher;
    }

    @Primary
    @Bean(name = "serverMessageEncode")
    @ConditionalOnMissingBean(name = "serverMessageEncode")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageEncode serverMessageEncode(@Qualifier("serverMessageDispatcher") ServerMessageDispatcher messageDispatcher) {
        ServerMessageEncode encode = new ServerMessageEncode(messageDispatcher) {};
        log.debug("init default ServerMessageEncode = {}", encode.hashCode());
        return encode;
    }

    @Primary
    @Bean(name = "serverMessageDecode")
    @ConditionalOnMissingBean(name = "serverMessageDecode")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDecode serverMessageDecode(BootstrapBuilder bootstrapBuilder,
                                                   @Qualifier("serverMessageDispatcher") ServerMessageDispatcher messageDispatcher) {
        ServerMessageDecode decode = new ServerMessageDecode(bootstrapBuilder, messageDispatcher) {};
        log.debug("init default ServerMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Primary
    @Bean(name = "socketService")
    public SocketService socketService(BootstrapBuilder bootstrapBuilder,
                                       @Qualifier("serverMessageDecode") ServerMessageDecode serverMessageDecode,
                                       @Qualifier("serverMessageEncode") ServerMessageEncode serverMessageEncode) throws Exception {

        return BootstrapBuilder.createSocketService(bootstrapBuilder, serverMessageDecode, serverMessageEncode, config);
    }


}
