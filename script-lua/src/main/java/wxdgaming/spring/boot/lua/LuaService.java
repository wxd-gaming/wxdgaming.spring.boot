package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.SpringReflect;
import wxdgaming.spring.boot.core.ann.AppStart;
import wxdgaming.spring.boot.lua.luac.spi.LuaSpi;

import java.io.Closeable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * lua 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 16:11
 */
@Slf4j
@Getter
@Service
public class LuaService implements AutoCloseable, Closeable {

    final ConcurrentHashMap<String, Object> globals = new ConcurrentHashMap<>();
    LuaRuntime luaRuntime;

    @Value("${lua.xpcall:true}")
    boolean xpcall;
    @Value("${lua.paths:}")
    String paths;

    @Order(100)
    @AppStart
    public void init(SpringReflect springReflect) {
        springReflect.content().withSuper(LuaSpi.class)
                .forEach(luaFunction -> {
                    globals.put(luaFunction.name(), luaFunction);
                });
    }


    @AppStart
    public void init() {
        LuaRuntime _luaRuntime = new LuaRuntime(LuaType.LUA54, "root", xpcall, paths, globals);
        LuaRuntime old = luaRuntime;
        luaRuntime = _luaRuntime;

        if (old != null) {
            new Thread(() -> {
                try {
                    Thread.sleep(30_000);
                    old.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    public void putGlobal(String key, Object value) {
        globals.put(key, value);
    }

    public long memory() {
        AtomicLong atomicLong = new AtomicLong();
        luaRuntime.memory(atomicLong);
        return atomicLong.get();
    }

    public long size() {
        return luaRuntime.size();
    }

    @Override public void close() {
        luaRuntime.close();
    }

}
