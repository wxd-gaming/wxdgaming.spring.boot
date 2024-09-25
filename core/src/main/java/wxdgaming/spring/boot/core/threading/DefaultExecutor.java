package wxdgaming.spring.boot.core.threading;

/**
 * default Executor
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:10
 **/
public class DefaultExecutor extends BaseScheduledExecutor {

    public DefaultExecutor(int coreSize) {
        super("default", coreSize);
    }

}
