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
public class RedisHash extends LuaSpi {

    final RedisTemplate<String, Object> redisTemplate;

    public RedisHash(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override public String name() {
        return "opsForHash";
    }

    @Override public Object doAction(Lua L, List<Object> args) {
        String method = String.valueOf(args.removeFirst());
        String key = String.valueOf(args.removeFirst());
        Object hashKey = args.get(0);
        if ("put".equals(method)) {
            redisTemplate.opsForHash().put(key, hashKey, args.get(1));
            return null;
        } else if ("putIfAbsent".equals(method)) {
            return redisTemplate.opsForHash().putIfAbsent(key, hashKey, args.get(1));
        } else if ("increment".equals(method)) {
            if (args.size() == 2) {
                return redisTemplate.opsForHash().increment(key, hashKey, ((Number) args.get(1)).longValue());
            } else {
                return redisTemplate.opsForHash().increment(key, hashKey, 1);
            }
        } else if ("delete".equals(method)) {
            return redisTemplate.opsForHash().delete(key, hashKey);
        } else if ("get".equals(method)) {
            return redisTemplate.opsForHash().get(key, hashKey);
        }
        throw new RuntimeException("无法识别, " + method + " - " + key);
    }

}
