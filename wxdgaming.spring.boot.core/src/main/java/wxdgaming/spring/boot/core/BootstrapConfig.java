package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.Setter;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 14:14
 **/
@Getter
@Setter
public class BootstrapConfig {

    private boolean debug = true;
    private int gid = 1;
    private int sid = 1;
    private String name;
    private String jwtKey;
    private String rpcToken;

}
