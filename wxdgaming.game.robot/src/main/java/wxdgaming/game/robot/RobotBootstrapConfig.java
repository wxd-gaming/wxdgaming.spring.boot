package wxdgaming.game.robot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wxdgaming.spring.boot.core.BootstrapConfig;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 14:14
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "boot")
public class RobotBootstrapConfig extends BootstrapConfig {

    private String loginUrl;

}
