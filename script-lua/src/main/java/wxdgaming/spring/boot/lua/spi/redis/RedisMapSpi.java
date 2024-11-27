package wxdgaming.spring.boot.lua.spi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.lua.LuaJavaSpi;

/**
 * redis set 接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-16 19:37
 **/
@Service
@ConditionalOnBean(RedisTemplate.class)
public class RedisMapSpi implements LuaJavaSpi {

    final RedisTemplate<String, Object> redisTemplate;

    public RedisMapSpi(RedisTemplate<String, Object> redisTemplate) {this.redisTemplate = redisTemplate;}

    @Override public String getName() {
        return "opsForHash";
    }

    @Override public Object doAction(Lua L, Object[] args) {
        if (args.length == 3) {
            redisTemplate.opsForHash().put(String.valueOf(args[0]), args[1], args[2]);
        } else if (args.length == 2) {
            return redisTemplate.opsForHash().get(String.valueOf(args[0]), args[1]);
        }
        return null;
    }

}
