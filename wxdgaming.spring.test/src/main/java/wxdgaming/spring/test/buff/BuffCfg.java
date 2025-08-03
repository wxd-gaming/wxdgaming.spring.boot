package wxdgaming.spring.test.buff;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.spring.test.TargetGroup;

/**
 * buff配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 21:00
 **/
@Getter
@Builder
public class BuffCfg {

    private int id;
    private String name;
    /** 客户端是否显示 */
    private boolean clientShow;
    /** 持续时间 */
    private long duration;
}
