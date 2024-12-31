package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.lua.luac.LuacContext;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * lua 装载器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 16:10
 */
@Slf4j
@Getter
public class LuaRuntime implements Closeable {

    final LuaType luaType;
    final boolean xpcall;
    final String name;

    final LuaFileRequire luaFileRequire;
    final LuaFileCache luaFileCache;

    final Map<String, Object> globals;
    volatile ILuaContext luajContext;
    volatile ConcurrentHashMap<Thread, ILuaContext> contexts = new ConcurrentHashMap<>();

    public LuaRuntime(LuaType luaType, String name, boolean xpcall, String dir, Map<String, Object> globals) {
        this.luaType = luaType;
        this.xpcall = xpcall;
        this.name = name;
        this.globals = Map.copyOf(globals);
        luaFileCache = new LuaFileCache(dir);
        luaFileRequire = new LuaFileRequire(luaFileCache);
    }

    public ILuaContext context() {
        ILuaContext luaContext = contexts.get(Thread.currentThread());
        if (luaContext == null || luaContext.isClosed()) {
            luaContext = new LuacContext(this);
            luaContext.call(true, "luainit");
            contexts.put(Thread.currentThread(), luaContext);
        }
        return luaContext;
    }

    /**
     * 单位KB
     *
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-10-18 17:40
     */
    public long memory() {
        AtomicLong memory = new AtomicLong();
        memory(memory);
        return memory.get();
    }

    public void memory(AtomicLong memory) {
        if (luajContext != null) memory0(memory, luajContext);
        contexts.values().forEach(luacContext -> memory0(memory, luacContext));
    }

    void memory0(AtomicLong memory, ILuaContext context) {
        synchronized (context) {
            context.memory(memory);
        }
    }

    public Object call(String key, Object... args) {
        return context().call(xpcall, key, args);
    }

    public long size() {
        int size = contexts.size();
        if (luajContext != null) size++;
        return size;
    }

    /** 关闭资源 */
    @Override public void close() {
        if (contexts == null) return;
        contexts.values().forEach(ILuaContext::close);
        contexts = null;
    }


}
