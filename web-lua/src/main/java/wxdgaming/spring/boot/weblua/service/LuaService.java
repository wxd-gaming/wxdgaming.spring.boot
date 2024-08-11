package wxdgaming.spring.boot.weblua.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.io.FileUtil;

import java.io.IOException;

/**
 * lua service
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-31 20:08
 **/
@Slf4j
@Getter
@Service
public class LuaService implements InitPrint {

    final LuaLoggerService logger;
    final RedisTemplate<Object, Object> redisTemplate;
    final ResponseService responseService;
    Globals globals;

    public LuaService(LuaLoggerService logger, RedisTemplate<Object, Object> redisTemplate, ResponseService responseService) {
        this.logger = logger;
        this.redisTemplate = redisTemplate;
        this.responseService = responseService;
    }

    @PostConstruct
    public void init() throws IOException {
        this.globals = JsePlatform.standardGlobals();
        this.globals.set("responseUtil", CoerceJavaToLua.coerce(responseService));
        this.globals.set("logger", CoerceJavaToLua.coerce(logger));
        this.globals.set("redisTemplate", CoerceJavaToLua.coerce(redisTemplate));
        log.debug("redisTemplate hashCode: {}", redisTemplate.hashCode());
        FileUtil
                .resourceStreams(this.getClass().getClassLoader(), "lua")
                .forEach(item -> {
                    try {
                        log.info("load lua {}", item.t1());
                        String string = FileReadUtil.readString(item.t2());
                        this.globals.load(string, item.t1()).call();
                    } catch (Exception e) {
                        log.error("load lua error", e);
                    }
                });
    }

    public LuaValue[] parse(Object... params) {
        LuaValue[] luaValues = new LuaValue[params.length];
        for (int i = 0; i < params.length; i++) {
            luaValues[i] = CoerceJavaToLua.coerce(params[i]);
        }
        return luaValues;
    }

    public LuaValue get(String key) {
        return this.globals.get(key);
    }

    public LuaValue func(String method, Object... params) {
        LuaValue luaValue = this.globals.get(method);
        LuaValue[] luaValues = parse(params);
        Varargs invoke = luaValue.invoke(luaValues);
        LuaValue ret = null;
        if (invoke != null && invoke != LuaValue.NONE && invoke != LuaValue.NIL) {
            ret = invoke.arg1();
        }
        return ret;
    }

}
