package wxdgaming.spring.boot.net;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.message.inner.pojo.InnerMessage;

/**
 * 内置的心跳定时器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 15:03
 **/
@Slf4j
@Controller
public class InnerHeartController {

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, InnerMessage.ReqHeart reqHeart) throws Exception {
        log.debug("{} {}", session, reqHeart.getMilli());
    }

}
