package wxdgaming.spring.boot.net;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.net.pojo.inner.InnerMessage;

/**
 * 内置的心跳定时器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 15:03
 **/
@Slf4j
@Controller
public class InnerHeartController {

    @Value("${socket.printLogger:false}")
    boolean printLogger = false;

    public InnerHeartController() {

    }

    @ProtoMapper
    public void reqHeartAction(SocketSession session, InnerMessage.ReqHeart reqHeart) throws Exception {
        if (printLogger)
            log.debug("内部心跳包{} {}", session, reqHeart.getMilli());
    }

    @ProtoMapper
    public void resHeartAction(SocketSession session, InnerMessage.ResHeart resHeart) throws Exception {
        if (printLogger)
            log.debug("内部心跳包{} {}", session, resHeart.getMilli());
    }

}
