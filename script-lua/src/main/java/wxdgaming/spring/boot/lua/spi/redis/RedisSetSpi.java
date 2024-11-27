package wxdgaming.spring.boot.lua.spi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.lua.LuaJavaSpi;

import java.util.concurrent.TimeUnit;

/**
 * redis set 接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-16 19:37
 **/
@Service()
@ConditionalOnBean(RedisTemplate.class)
public class RedisSetSpi implements LuaJavaSpi {

    final RedisTemplate<String, Object> redisTemplate;

    public RedisSetSpi(RedisTemplate<String, Object> redisTemplate) {this.redisTemplate = redisTemplate;}

    @Override public String getName() {
        return "opsForValue";
    }

    @Override public Object doAction(Lua L, Object[] args) {
        if (args.length == 4) {
            redisTemplate.opsForValue().set(String.valueOf(args[0]), args[1], ((Number) args[2]).longValue(), (TimeUnit) args[3]);
        } else if (args.length == 3) {
            redisTemplate.opsForValue().set(String.valueOf(args[0]), args[1], ((Number) args[2]).longValue(), TimeUnit.MILLISECONDS);
        } else if (args.length == 2) {
            redisTemplate.opsForValue().set(String.valueOf(args[0]), args[1]);
        } else {
            return redisTemplate.opsForValue().get(args[0]);
        }
        return null;
    }

}
