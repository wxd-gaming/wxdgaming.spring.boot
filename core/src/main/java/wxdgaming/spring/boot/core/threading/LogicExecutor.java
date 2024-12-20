package wxdgaming.spring.boot.core.threading;

import lombok.Getter;

/**
 * logic Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
public class LogicExecutor extends BaseScheduledExecutor {

    @Getter private static LogicExecutor ins = null;

    public LogicExecutor(int coreSize) {
        super("logic", coreSize);
        ins = this;
    }

}
