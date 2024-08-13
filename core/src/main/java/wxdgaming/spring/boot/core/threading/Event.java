package wxdgaming.spring.boot.core.threading;

import wxdgaming.spring.boot.core.GlobalUtil;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 15:02
 **/
public interface Event extends Runnable {


    @Override default void run() {
        try {
            onEvent();
        } catch (Throwable throwable) {
            GlobalUtil.exception(this.getClass().getName(), throwable);
        }
    }

    /** 事件执行器 */
    void onEvent() throws Throwable;

}
