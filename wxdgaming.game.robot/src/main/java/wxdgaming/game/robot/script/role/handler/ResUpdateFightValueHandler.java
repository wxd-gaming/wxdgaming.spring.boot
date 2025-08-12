package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.role.ResUpdateFightValue;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 更新战斗力
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ResUpdateFightValueHandler {

    /** 更新战斗力 */
    @ProtoRequest
    public void resUpdateFightValue(SocketSession socketSession, ResUpdateFightValue req) {

    }

}