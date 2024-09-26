package wxdgaming.spring.boot.net;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired BootstrapBuilder bootstrapBuilder;

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, InnerMessage.ReqHeart reqHeart) throws Exception {
        if (bootstrapBuilder.isPrintLogger())
            log.info("{} {}", session, reqHeart.getMilli());
    }

}
