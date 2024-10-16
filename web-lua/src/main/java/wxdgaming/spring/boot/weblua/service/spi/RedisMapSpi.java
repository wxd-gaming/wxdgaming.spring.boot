package wxdgaming.spring.boot.weblua.service.spi;

import org.springframework.stereotype.Service;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.weblua.service.LuaFunctionSpi;

/**
 * redis set 接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-16 19:37
 **/
@Service()
public class RedisMapSpi extends LuaFunctionSpi {

    @Override public String getName() {
        return "opsForHash";
    }

    @Override public Object doAction(Lua L, Object[] args) {
        if (args.length == 3) {
            redisTemplate.opsForHash().put(args[0], args[1], args[2]);
        } else if (args.length == 2) {
            return redisTemplate.opsForHash().get(args[0], args[1]);
        }
        return null;
    }

}
