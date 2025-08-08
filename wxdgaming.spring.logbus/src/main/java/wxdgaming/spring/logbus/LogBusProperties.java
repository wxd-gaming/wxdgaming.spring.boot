package wxdgaming.spring.logbus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-08 13:16
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "logbus")
public class LogBusProperties {

    private int splitOrg = 300;
    private boolean gzip = true;
    private String filePath = "target/logbus";
    private String postUrl = "http://127.0.0.1:8888/logbus/log";
    private String token = "ddddd";

}
