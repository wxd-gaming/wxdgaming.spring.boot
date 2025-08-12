package wxdgaming.game.server.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.role.ReqLogout;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * null
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqLogoutHandler {

    /** null */
    @ProtoRequest
    public void reqLogout(SocketSession socketSession, ReqLogout req, @ThreadParam(path = "clientSessionMapping") ClientSessionMapping clientSessionMapping) {

    }

}