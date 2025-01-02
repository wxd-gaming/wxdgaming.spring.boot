package wxdgaming.spring.boot.core.threading;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * logic Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
@Component
public class LogicExecutor extends BaseScheduledExecutor {

    public LogicExecutor(@Value("${server.executor.logicCoreSize:10}") int coreSize) {
        super("logic", coreSize);
    }

}
