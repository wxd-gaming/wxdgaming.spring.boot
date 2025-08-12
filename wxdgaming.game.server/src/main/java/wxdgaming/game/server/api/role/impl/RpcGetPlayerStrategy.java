package wxdgaming.game.server.api.role.impl;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.module.inner.RpcService;
import wxdgaming.game.global.bean.role.PlayerSnap;
import wxdgaming.game.server.api.role.GetPlayerStrategy;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.role.RoleEntity;
import wxdgaming.game.server.module.data.ClientSessionService;
import wxdgaming.game.server.module.data.GlobalDbDataCenterService;

/**
 * 通过数据库获取player
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-18 20:39
 **/
@Slf4j
public class RpcGetPlayerStrategy implements GetPlayerStrategy {

    final RpcService rpcService;
    final ClientSessionService clientSessionService;
    final GlobalDbDataCenterService globalDbDataCenterService;

    public RpcGetPlayerStrategy(RpcService rpcService, ClientSessionService clientSessionService, GlobalDbDataCenterService globalDbDataCenterService) {
        this.rpcService = rpcService;
        this.clientSessionService = clientSessionService;
        this.globalDbDataCenterService = globalDbDataCenterService;
    }

    SocketSession socketSession(long rid) {
        PlayerSnap playerSnap = globalDbDataCenterService.playerSnap(rid);
        int sid = playerSnap.getSid();
        return clientSessionService.getCrossServerSocketSessionMapping().get(sid);
    }

    @Override public RoleEntity roleEntity(long rid) {
        return null;
    }

    @Override public Player getPlayer(long rid) {
        return null;
    }

    @Override public void putCache(RoleEntity roleEntity) {

    }

    @Override public void save(RoleEntity roleEntity) {

    }
}
