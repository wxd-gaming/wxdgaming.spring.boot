package wxdgaming.spring.boot.lua;

import org.springframework.context.annotation.ComponentScan;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 20:53
 **/
@ComponentScan
@ComponentScan(basePackageClasses = {CoreScan.class, DataRedisScan.class})
public class LuaScan {
}
