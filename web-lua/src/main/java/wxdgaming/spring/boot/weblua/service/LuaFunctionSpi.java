package wxdgaming.spring.boot.weblua.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import wxdgaming.spring.boot.lua.LuaFunction;

/**
 * lua 功能接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-16 19:36
 **/
public abstract class LuaFunctionSpi implements LuaFunction {

    @Autowired protected RedisTemplate<Object, Object> redisTemplate;

    public abstract String getName();

}
