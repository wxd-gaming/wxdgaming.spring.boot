package wxdgaming.game.robot.script.bag.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.bag.ResUpdateBagInfo;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 响应背包信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateBagInfoHandler {

    /** 响应背包信息 */
    @ProtoRequest
    public void resUpdateBagInfo(SocketSession socketSession, ResUpdateBagInfo req) {
        Robot robot = socketSession.bindData("robot");
        log.info("{} 背包更新响应：\n{}", robot, req.toJSONString());
        robot.getItems().entrySet().removeIf(v -> req.getDelItemIds().contains(v.getKey()));
        robot.getItems().putAll(req.getChangeItems());
        robot.getCurrencyMap().putAll(req.getCurrencyMap());
    }

}