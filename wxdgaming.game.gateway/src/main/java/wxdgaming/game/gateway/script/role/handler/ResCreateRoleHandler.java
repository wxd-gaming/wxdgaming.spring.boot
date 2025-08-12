package wxdgaming.game.gateway.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.message.role.ResCreateRole;

/**
 * 创建角色响应
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ResCreateRoleHandler {

    /** 创建角色响应 */
    public void resCreateRole(SocketSession socketSession, ResCreateRole req, @ThreadParam(path = "forwardMessage") InnerForwardMessage forwardMessage) {
        
    }

}