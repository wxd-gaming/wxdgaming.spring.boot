package wxdgaming.game.gateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 14:14
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "boot")
public class GatewayBootstrapConfig extends wxdgaming.spring.boot.core.BootstrapConfig {

    private int maxOnline = 2000;
    private String loginUrl;

}
