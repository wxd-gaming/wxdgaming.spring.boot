package wxdgaming.spring.boot.weblua.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.lua.LuaLogger;
import wxdgaming.spring.boot.lua.LuaRuntime;

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

    final RedisTemplate<Object, Object> redisTemplate;
    final LuaResponseService luaResponseService;
    LuaRuntime globals;

    public LuaService(RedisTemplate<Object, Object> redisTemplate, LuaResponseService luaResponseService) {
        this.redisTemplate = redisTemplate;
        this.luaResponseService = luaResponseService;
    }

    @PostConstruct
    public void init() {
        this.globals = new LuaRuntime("main");
        this.globals.set("responseUtil", luaResponseService);
        this.globals.set("jlog", LuaLogger.getIns());
        this.globals.set("redisTemplate", redisTemplate);
        log.debug("redisTemplate hashCode: {}", redisTemplate.hashCode());
        FileUtil
                .resourceStreams(this.getClass().getClassLoader(), "lua")
                .forEach(item -> {
                    try {
                        log.info("load lua {}", item.t1());
                        String string = FileReadUtil.readString(item.t2());
                        this.globals.load(string, item.t1());
                    } catch (Exception e) {
                        log.error("load lua error", e);
                    }
                });
    }

}
