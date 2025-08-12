package wxdgaming.game.cfg.bean;


import lombok.Getter;
import wxdgaming.game.cfg.bean.mapping.QActivityMapping;
import wxdgaming.spring.boot.excel.store.DataChecked;
import wxdgaming.spring.boot.excel.store.DataTable;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 活动, src/main/cfg/活动.xlsx, q_activity,
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-05 10:08:09
 **/
@Getter
public class QActivity extends QActivityMapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
