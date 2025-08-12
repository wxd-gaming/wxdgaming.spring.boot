package wxdgaming.game.server.script.cdkey.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.cdkey.ReqUseCdKey;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.cdkey.CDKeyService;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.core.executor.ExecutorWith;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 请求使用cdkey
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ReqUseCdKeyHandler {

    private final CDKeyService cdKeyService;

    public ReqUseCdKeyHandler(CDKeyService cdKeyService) {
        this.cdKeyService = cdKeyService;
    }

    /** 请求使用cdkey */
    @ProtoRequest
    @ExecutorWith(queueName = "use-cdKey")
    public void reqUseCdKey(SocketSession socketSession, ReqUseCdKey req,
                            @ThreadParam(path = "player") Player player) {
        cdKeyService.use(player, req.getCdKey());
    }

}