package wxdgaming.spring.boot.weblua.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.lua.LuaLogger;
import wxdgaming.spring.boot.lua.LuaRuntime;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

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
    final AtomicReference<LuaRuntime> luaRuntime = new AtomicReference<>();

    public LuaService(RedisTemplate<Object, Object> redisTemplate, LuaResponseService luaResponseService) {
        this.redisTemplate = redisTemplate;
        this.luaResponseService = luaResponseService;
    }

    @PostConstruct
    public void init() {
        LuaRuntime main = new LuaRuntime("main", new Path[]{Paths.get("lua")});
        main.getGlobals().put("responseUtil", luaResponseService);
        main.getGlobals().put("jlog", LuaLogger.getIns());
        main.getGlobals().put("opsForValue", redisTemplate.opsForValue());
        log.debug("redisTemplate hashCode: {}", redisTemplate.hashCode());
        LuaRuntime tmp = luaRuntime.get();
        if (tmp != null) {
            CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(20_000);
                        } catch (InterruptedException ignore) {}
                        tmp.close();
                    })
                    .exceptionally(exception -> {
                        log.error("释放资源", exception);
                        return null;
                    });
        }
        luaRuntime.set(main);
        luaRuntime.get().context().pCall("root");
    }

}
