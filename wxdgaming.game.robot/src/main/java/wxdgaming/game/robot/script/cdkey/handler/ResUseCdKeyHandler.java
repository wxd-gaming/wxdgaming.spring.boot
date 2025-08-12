package wxdgaming.game.robot.script.cdkey.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.cdkey.ResUseCdKey;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 响应使用cdkey
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUseCdKeyHandler {

    /** 响应使用cdkey */
    @ProtoRequest
    public void resUseCdKey(SocketSession socketSession, ResUseCdKey req) {

    }

}