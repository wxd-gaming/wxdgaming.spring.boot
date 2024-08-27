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

import java.util.concurrent.CompletableFuture;

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
    LuaRuntime luaRuntime;

    public LuaService(RedisTemplate<Object, Object> redisTemplate, LuaResponseService luaResponseService) {
        this.redisTemplate = redisTemplate;
        this.luaResponseService = luaResponseService;
    }

    @PostConstruct
    public void init() {
        LuaRuntime main = new LuaRuntime("main");
        main.set("responseUtil", luaResponseService);
        main.set("jlog", LuaLogger.getIns());
        main.set("redisTemplate", redisTemplate);
        log.debug("redisTemplate hashCode: {}", redisTemplate.hashCode());
        FileUtil
                .resourceStreams(this.getClass().getClassLoader(), "lua")
                .forEach(item -> {
                    try {
                        String string = FileReadUtil.readString(item.t2());
                        main.load(item.t1(), string);
                    } catch (Exception e) {
                        log.error("load lua error {}", item.t1(), e);
                    }
                });
        LuaRuntime tmp = luaRuntime;
        if (tmp != null) {
            CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ignore) {}
                        tmp.close();
                    })
                    .exceptionally(exception -> {
                        log.error("释放资源", exception);
                        return null;
                    });
        }
        luaRuntime = main;
    }

}
