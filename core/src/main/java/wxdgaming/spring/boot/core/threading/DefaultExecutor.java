package wxdgaming.spring.boot.core.threading;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * default Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
@Component
public class DefaultExecutor extends BaseScheduledExecutor implements InitPrint {

    public DefaultExecutor(@Value("${defaultCoreSize:2}") int coreSize) {
        super("default", coreSize);
    }

}
