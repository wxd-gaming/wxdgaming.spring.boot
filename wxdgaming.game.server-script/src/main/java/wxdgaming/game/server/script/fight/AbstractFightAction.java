package wxdgaming.game.server.script.fight;

import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.game.server.bean.MapNpc;

/**
 * 战斗处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 09:48
 **/
public abstract class AbstractFightAction extends HoldRunApplication {

    @Autowired protected FightService fightService;

    public abstract int type();

    public abstract void doAction(MapNpc mapNpc, Object... args);

}
