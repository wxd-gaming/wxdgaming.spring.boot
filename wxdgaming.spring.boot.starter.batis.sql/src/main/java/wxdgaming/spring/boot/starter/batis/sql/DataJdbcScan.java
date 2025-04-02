package wxdgaming.spring.boot.starter.batis.sql;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import wxdgaming.spring.boot.starter.core.CoreScan;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 09:25
 **/
@EntityScan
@ComponentScan
@ComponentScan(basePackageClasses = {CoreScan.class})
public class DataJdbcScan {

}
