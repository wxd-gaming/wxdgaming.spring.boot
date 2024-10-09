package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lua 装载器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 16:10
 */
@Slf4j
@Getter
public class LuaRuntime implements Closeable {

    final String name;
    final Path[] paths;
    final ConcurrentHashMap<String, Object> globals = new ConcurrentHashMap<>();
    ConcurrentHashMap<Thread, LuaContext> threadLocal = new ConcurrentHashMap<>();


    public LuaRuntime(String name, Path[] paths) {
        this.name = name;
        this.paths = paths;
    }

    public LuaContext newContext() {
        return new LuaContext(globals, paths);
    }

    public LuaContext context() {
        LuaContext luaContext = threadLocal.get(Thread.currentThread());
        if (luaContext == null || luaContext.isClosed()) {
            luaContext = newContext();
            threadLocal.put(Thread.currentThread(), luaContext);
        }
        return luaContext;
    }

    /** 关闭资源 */
    @Override public void close() {
        threadLocal.values().forEach(LuaContext::close);
        threadLocal = null;
    }
}
