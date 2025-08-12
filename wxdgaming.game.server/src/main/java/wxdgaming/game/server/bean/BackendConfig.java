package wxdgaming.game.server.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-14 13:42
 **/
@Getter
@Setter
public class BackendConfig extends ObjectBase {

    private int gameId;
    private String url;
    private String appToken;
    private String logToken;

}
