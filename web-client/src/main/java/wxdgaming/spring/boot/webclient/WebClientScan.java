package wxdgaming.spring.boot.webclient;

import org.springframework.context.annotation.ComponentScan;
import wxdgaming.spring.boot.core.CoreScan;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 14:52
 **/
@ComponentScan
@ComponentScan(basePackageClasses = {CoreScan.class})
public class WebClientScan {
}
