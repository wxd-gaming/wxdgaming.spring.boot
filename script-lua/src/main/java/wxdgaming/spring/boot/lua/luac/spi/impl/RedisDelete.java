package wxdgaming.spring.boot.lua.luac.spi.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.lua.luac.spi.LuaSpi;

import java.util.List;

/**
 * redis
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-30 19:59
 **/
@Component()
@ConditionalOnBean(RedisTemplate.class)
public class RedisDelete extends LuaSpi {

    final RedisTemplate<String, Object> redisTemplate;

    public RedisDelete(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override public String name() {
        return "redisDelete";
    }

    @Override public Object doAction(Lua L, List<Object> args) {
        String key = String.valueOf(args.removeFirst());
        return redisTemplate.delete(key);
    }

}
