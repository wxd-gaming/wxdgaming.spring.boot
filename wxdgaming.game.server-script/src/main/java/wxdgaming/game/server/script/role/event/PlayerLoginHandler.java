package wxdgaming.game.server.script.role.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.StatusConst;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnLogin;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.event.OnTask;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.core.lang.bit.BitFlag;
import wxdgaming.spring.boot.core.lang.condition.Condition;
import wxdgaming.spring.boot.core.timer.MyClock;

/**
 * 角色创建事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 19:51
 **/
@Slf4j
@Component
public class PlayerLoginHandler extends HoldRunApplication {

    public PlayerLoginHandler() {
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        log.info("玩家上线:{} {}", ThreadContext.context().queueName(), player);
        player.setStatus(new BitFlag());
        /*触发任务登录次数*/
        runApplication.executorWithMethodAnnotatedIgnoreException(OnTask.class, player, new Condition("login", 1));
        if (!MyClock.isSameDay(player.getOnlineInfo().getLastLoginDayTime())) {
            player.getOnlineInfo().setLastLoginDayTime(MyClock.millis());
            /*触发任务登录天数*/
            runApplication.executorWithMethodAnnotatedIgnoreException(OnTask.class, player, new Condition("loginDay", 1));
        }
    }

    /** 创建角色之后赠送初始化道具 */
    @Order(1)
    @OnLogin
    public void onLogin(Player player) {
        log.info("玩家上线:{} {}", ThreadContext.context().queueName(), player);
        player.getStatus().addFlags(StatusConst.Online);
        player.getOnlineInfo().setLastLoginTime(MyClock.millis());
    }


}
