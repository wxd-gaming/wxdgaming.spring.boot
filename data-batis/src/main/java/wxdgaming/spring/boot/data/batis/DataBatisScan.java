package wxdgaming.spring.boot.data.batis;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import wxdgaming.spring.boot.core.CoreScan;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 09:25
 **/
@EntityScan
@ComponentScan
@ComponentScan(basePackageClasses = {CoreScan.class})
public class DataBatisScan {

}
