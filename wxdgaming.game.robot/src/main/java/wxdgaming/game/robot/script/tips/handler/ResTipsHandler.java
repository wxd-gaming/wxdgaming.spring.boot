package wxdgaming.game.robot.script.tips.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.tips.ResTips;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 提示内容
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ResTipsHandler {

    /** 提示内容 */
    @ProtoRequest
    public void resTips(SocketSession socketSession, ResTips req) {

        log.info("收到提示内容:{} {}", req.getType(), req.getContent());

    }

}