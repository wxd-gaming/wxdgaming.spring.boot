package wxdgaming.game.robot.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.bag.ResBagInfo;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 响应背包信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ResBagInfoHandler {

    /** 响应背包信息 */
    @ProtoRequest
    public void resBagInfo(SocketSession socketSession, ResBagInfo req) {
        Robot robot = socketSession.bindData("robot");
        log.info("{} 背包响应：\n{}", robot, req.toJSONString());
        robot.setItems(req.getItems());
        robot.setCurrencyMap(req.getCurrencyMap());
    }

}