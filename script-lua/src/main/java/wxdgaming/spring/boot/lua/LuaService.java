package wxdgaming.spring.boot.lua;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * lua 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 16:11
 */
@Slf4j
public class LuaService implements AutoCloseable, Closeable {

    LuaRuntime luaRuntime;

    public static LuaService of(LuacType luacType, boolean xpcall, String paths) {
        LuaService luaService = new LuaService(luacType, xpcall, paths);
        luaService.init();
        return luaService;
    }

    LuacType luacType;
    boolean xpcall;
    String paths;

    private LuaService(LuacType luacType, boolean xpcall, String paths) {
        this.luacType = luacType;
        this.xpcall = xpcall;
        this.paths = paths;
    }

    public void init() {

        LuaRuntime _luaRuntime = new LuaRuntime(luacType, "root", xpcall, new Path[]{Paths.get(paths)});
        final LuaRuntime old = luaRuntime;
        luaRuntime = _luaRuntime;

        if (old != null) {
            DefaultExecutor.getIns().schedule(old::close, 30_000, TimeUnit.MILLISECONDS);
        }
    }

    public LuaRuntime getRuntime() {
        return luaRuntime;
    }

    public void set(String key, LuaFunction value) {
        luaRuntime.getGlobals().put(key, value);
    }

    public void set(String key, Object value) {
        luaRuntime.getGlobals().put(key, value);
    }

    /** 把一个方法转化成函数传递给lua */
    public void pushJavaFunction(Object bean, Method method) {
        pushJavaFunction(bean, method.getName(), method);
    }

    /** 把一个方法转化成函数传递给lua */
    public void pushJavaFunction(final Object bean, String key, Method method) {
        LuaFunction jFunction = new LuaFunction() {
            @Override public Object doAction(Lua L, Object[] args) {
                try {
                    return method.invoke(bean, args);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        };
        set(key, jFunction);
    }

    @Override public void close() {
        luaRuntime.close();
    }

}
