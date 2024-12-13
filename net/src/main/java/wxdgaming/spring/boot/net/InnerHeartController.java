package wxdgaming.spring.boot.net;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.net.message.inner.InnerMessage;

/**
 * 内置的心跳定时器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 15:03
 **/
@Slf4j
@Controller
public class InnerHeartController {

    final BootstrapBuilder bootstrapBuilder;

    public InnerHeartController(BootstrapBuilder bootstrapBuilder) {this.bootstrapBuilder = bootstrapBuilder;}

    @MsgMapper
    public void reqHeartAction(SocketSession session, InnerMessage.ReqHeart reqHeart) throws Exception {
        if (bootstrapBuilder.isPrintLogger())
            log.info("{} {}", session, reqHeart.getMilli());
    }

    @MsgMapper
    public void resHeartAction(SocketSession session, InnerMessage.ResHeart resHeart) throws Exception {
        if (bootstrapBuilder.isPrintLogger())
            log.info("{} {}", session, resHeart.getMilli());
    }

}
