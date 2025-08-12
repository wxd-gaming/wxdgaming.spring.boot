package wxdgaming.spring.boot.excel.store;

import java.util.Map;

/**
 * 用于配置的检测和初始化
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2021-10-21 13:48
 **/
public interface DataChecked {

    /** 用于配置的检测和初始化 */
    default void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {}

}
