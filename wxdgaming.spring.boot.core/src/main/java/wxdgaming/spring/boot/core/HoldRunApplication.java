package wxdgaming.spring.boot.core;

import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.ann.Init;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 10:53
 **/
public abstract class HoldRunApplication implements InitPrint {

    protected ApplicationContextProvider runApplication;

    @Init
    @Order(Integer.MIN_VALUE)
    public void initHold(ApplicationContextProvider runApplication) {
        this.runApplication = runApplication;
    }

}
