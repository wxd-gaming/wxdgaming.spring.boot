package wxdgaming.game.server.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.bag.ReqBagInfo;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 请求背包信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ReqBagInfoHandler {

    final BagService bagService;

    public ReqBagInfoHandler(BagService bagService) {
        this.bagService = bagService;
    }

    /** 请求背包信息 */
    @ProtoRequest
    public void reqBagInfo(SocketSession socketSession, ReqBagInfo req, @ThreadParam(path = "player") Player player) {
        bagService.sendBagInfo(player, req.getBagType());
    }

}