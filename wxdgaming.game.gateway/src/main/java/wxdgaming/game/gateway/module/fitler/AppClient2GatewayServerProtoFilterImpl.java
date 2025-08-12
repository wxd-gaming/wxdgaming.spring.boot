package wxdgaming.game.gateway.module.fitler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.pojo.ProtoListenerFactory;
import wxdgaming.spring.boot.net.pojo.ProtoListenerTrigger;
import wxdgaming.spring.boot.net.pojo.ServerProtoFilter;

/**
 * 客户端传递到网关
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-28 15:19
 **/
@Slf4j
@Component
public class AppClient2GatewayServerProtoFilterImpl implements ServerProtoFilter {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory listenerFactory;


    public AppClient2GatewayServerProtoFilterImpl(DataCenterService dataCenterService, ProtoListenerFactory listenerFactory) {
        this.dataCenterService = dataCenterService;
        this.listenerFactory = listenerFactory;
    }


    @Override public boolean doFilter(ProtoListenerTrigger protoListenerTrigger) {
        SocketSession socketSession = protoListenerTrigger.getSocketSession();
        if (socketSession.getType() == SocketSession.Type.server) {
            /*表示对外的session*/
            return true;
        }
        return true;
    }

}
