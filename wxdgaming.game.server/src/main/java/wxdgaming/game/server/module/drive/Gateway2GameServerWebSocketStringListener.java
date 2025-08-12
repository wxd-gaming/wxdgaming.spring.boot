package wxdgaming.game.server.module.drive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.server.IServerWebSocketStringListener;

/**
 * 驱动
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-17 21:24
 **/
@Slf4j
@Component
public class Gateway2GameServerWebSocketStringListener implements IServerWebSocketStringListener {

    @Override public void onMessage(SocketSession socketSession, String message) {
        log.debug("{} String Listener {}", socketSession, message);
    }

}
