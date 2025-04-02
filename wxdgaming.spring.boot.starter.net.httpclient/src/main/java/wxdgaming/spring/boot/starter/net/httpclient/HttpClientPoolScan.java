package wxdgaming.spring.boot.starter.net.httpclient;

import org.springframework.context.annotation.ComponentScan;
import wxdgaming.spring.boot.starter.core.CoreScan;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-01 20:12
 **/
@ComponentScan
@ComponentScan(basePackageClasses = {CoreScan.class})
public class HttpClientPoolScan {

    public static final HttpClientConfig DEFAULT = new HttpClientConfig(
            20, 300,
            30,
            3000, 3000, 3000, 30000,
            "TLS",
            true
    );

}
