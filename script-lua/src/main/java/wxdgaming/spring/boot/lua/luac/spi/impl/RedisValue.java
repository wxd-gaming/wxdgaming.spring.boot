package wxdgaming.spring.boot.lua.luac.spi.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.lua.luac.spi.LuaSpi;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-30 19:59
 **/
@Component()
@ConditionalOnBean(RedisTemplate.class)
public class RedisValue extends LuaSpi {

    final RedisTemplate<String, Object> redisTemplate;

    public RedisValue(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override public String name() {
        return "redisValue";
    }

    @Override public Object doAction(Lua L, List<Object> args) {
        String method = String.valueOf(args.removeFirst());
        String key = String.valueOf(args.removeFirst());

        if ("set".equalsIgnoreCase(method)) {
            Object value = args.get(0);
            if (args.size() == 3) {
                redisTemplate.opsForValue().set(key, value, ((Number) args.get(1)).longValue(), (TimeUnit) args.get(3));
            } else if (args.size() == 2) {
                redisTemplate.opsForValue().set(key, value, ((Number) args.get(1)).longValue(), TimeUnit.MILLISECONDS);
            } else if (args.size() == 1) {
                redisTemplate.opsForValue().set(key, value);
            }
            return null;
        } else if ("setIfAbsent".equalsIgnoreCase(method)) {
            Object value = args.get(0);
            if (args.size() == 3) {
                return redisTemplate.opsForValue().setIfAbsent(key, value, ((Number) args.get(1)).longValue(), (TimeUnit) args.get(3));
            } else if (args.size() == 2) {
                return redisTemplate.opsForValue().setIfAbsent(key, value, ((Number) args.get(1)).longValue(), TimeUnit.MILLISECONDS);
            } else if (args.size() == 1) {
                return redisTemplate.opsForValue().setIfAbsent(key, value);
            }
        } else if ("increment".equalsIgnoreCase(method)) {
            if (args.size() == 1) {
                return redisTemplate.opsForValue().increment(key, ((Number) args.getFirst()).longValue());
            }
            return redisTemplate.opsForValue().increment(key);
        } else if ("decrement".equalsIgnoreCase(method)) {
            if (args.size() == 1) {
                return redisTemplate.opsForValue().decrement(key, ((Number) args.getFirst()).longValue());
            }
            return redisTemplate.opsForValue().decrement(key);
        } else {
            return redisTemplate.opsForValue().get(key);
        }

        throw new RuntimeException("无法识别, " + method + " - " + key);
    }

}
