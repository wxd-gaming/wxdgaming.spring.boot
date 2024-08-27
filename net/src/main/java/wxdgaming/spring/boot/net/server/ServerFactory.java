package wxdgaming.spring.boot.net.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.net.MessageDispatcher;

/**
 * 服务工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-27 09:11
 **/
@Slf4j
@Configuration
public class ServerFactory {

    @Bean
    @ConditionalOnMissingBean(ServerMessageEncode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageEncode serverMessageEncode(MessageDispatcher messageDispatcher) {
        ServerMessageEncode encode = new ServerMessageEncode(messageDispatcher) {};
        log.debug("init default ServerMessageEncode = {}", encode.hashCode());
        return encode;
    }

    @Bean
    @ConditionalOnMissingBean(ServerMessageDecode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDecode serverMessageDecode(MessageDispatcher messageDispatcher) {
        ServerMessageDecode decode = new ServerMessageDecode(messageDispatcher) {};
        log.debug("init default ServerMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(SocketServerDeviceHandler.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public SocketServerDeviceHandler socketServerDeviceHandler() {
        SocketServerDeviceHandler deviceHandler = new SocketServerDeviceHandler();
        log.debug("init default SocketServerDeviceHandler = {}", deviceHandler.hashCode());
        return deviceHandler;
    }
}
