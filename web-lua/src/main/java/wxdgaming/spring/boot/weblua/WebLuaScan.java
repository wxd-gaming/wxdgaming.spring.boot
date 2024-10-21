package wxdgaming.spring.boot.weblua;

import org.springframework.context.annotation.ComponentScan;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.lua.LuaScan;

/**
 * web lua 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-11 09:43
 **/
@ComponentScan
@ComponentScan(basePackageClasses = {CoreScan.class, DataRedisScan.class, LuaScan.class})
public class WebLuaScan {
}
