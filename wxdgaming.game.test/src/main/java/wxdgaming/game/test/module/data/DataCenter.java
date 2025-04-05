package wxdgaming.game.test.module.data;

import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.starter.core.InitPrint;
import wxdgaming.spring.boot.starter.core.SpringReflect;
import wxdgaming.spring.boot.starter.core.ann.AppStart;

/**
 * 数据中心
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-05 19:19
 **/
@Component
public class DataCenter implements InitPrint {


    @AppStart
    public void start(SpringReflect springReflect) {

    }

}
