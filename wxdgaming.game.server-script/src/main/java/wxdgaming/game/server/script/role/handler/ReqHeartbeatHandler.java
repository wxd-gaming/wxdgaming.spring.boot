package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.game.message.role.ReqHeartbeat;
import wxdgaming.game.message.role.ResHeartbeat;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;

/**
 * 心跳包
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ReqHeartbeatHandler {

    final DataCenterService dataCenterService;

    public ReqHeartbeatHandler(DataCenterService dataCenterService) {
        this.dataCenterService = dataCenterService;
    }

    /** 心跳包 */
    @ProtoRequest
    public void reqHeartbeat(SocketSession socketSession, ReqHeartbeat req) {
        ClientSessionMapping clientSessionMapping = ThreadContext.context("clientSessionMapping");
        ResHeartbeat resHeartbeat = new ResHeartbeat();
        resHeartbeat.setTimestamp(MyClock.millis());
        Player player = dataCenterService.getPlayer(clientSessionMapping.getRid());
        clientSessionMapping.forwardMessage(player, resHeartbeat);
    }

}