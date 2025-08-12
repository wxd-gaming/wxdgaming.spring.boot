package wxdgaming.game.robot.script.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.global.ResUpdateAttr;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 更新属性
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateAttrHandler {

    /** 更新属性 */
    @ProtoRequest
    public void resUpdateAttr(SocketSession socketSession, ResUpdateAttr req) {

    }

}