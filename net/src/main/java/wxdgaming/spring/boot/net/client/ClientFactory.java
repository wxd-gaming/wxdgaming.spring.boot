package wxdgaming.spring.boot.net.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.net.MessageDispatcher;

/**
 * 客户端工厂
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-27 09:12
 **/
@Slf4j
@Configuration
public class ClientFactory {

    @Bean
    @ConditionalOnMissingBean(ClientMessageEncode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageEncode clientMessageEncode(MessageDispatcher messageDispatcher) {
        ClientMessageEncode decode = new ClientMessageEncode(messageDispatcher) {};
        log.debug("init default ClientMessageEncode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(ClientMessageDecode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageDecode clientMessageDecode(MessageDispatcher messageDispatcher) {
        ClientMessageDecode decode = new ClientMessageDecode(messageDispatcher) {};
        log.debug("init default ClientMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(SocketClientDeviceHandler.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public SocketClientDeviceHandler socketClientDeviceHandler() {
        SocketClientDeviceHandler deviceHandler = new SocketClientDeviceHandler();
        log.debug("init default SocketClientDeviceHandler = {}", deviceHandler.hashCode());
        return deviceHandler;
    }

}
