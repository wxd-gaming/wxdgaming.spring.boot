package wxdgaming.game.chart;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;

/**
 * 链接登录服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:08
 **/
@Getter
@Setter
public class ChartConfig extends ObjectBase {

    private String url;
    private String jwtKey;

}
