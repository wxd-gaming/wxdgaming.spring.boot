package wxdgaming.spring.boot.lua;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * lua 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 16:11
 */
@Slf4j
public class LuaService implements AutoCloseable, Closeable {

    private volatile HashMap<String, LuaRuntime> runtimeHashMap = new HashMap<>();

    public static LuaService of(LuacType luacType, boolean useModule, boolean xpcall, String paths) {
        LuaService luaService = new LuaService(luacType, useModule, xpcall, paths);
        luaService.init();
        return luaService;
    }

    LuacType luacType;
    boolean useModule;
    boolean xpcall;
    String paths;

    private LuaService(LuacType luacType, boolean useModule, boolean xpcall, String paths) {
        this.luacType = luacType;
        this.useModule = useModule;
        this.xpcall = xpcall;
        this.paths = paths;
    }

    public void init() {
        HashMap<String, LuaRuntime> tmpRuntimeHashMap = new HashMap<>();
        if (useModule) {
            Path script_path = Paths.get(paths + "/script");
            try {
                Files.walk(script_path, 1)
                        .filter(Files::isDirectory)
                        .forEach(dir -> {
                            if (dir.equals(script_path)) return;
                            log.info("load lua module：{} - {}", dir, dir.getFileName());
                            LuaRuntime luaRuntime = new LuaRuntime(luacType, dir.getFileName().toString(), xpcall, new Path[]{dir, Paths.get(paths + "/util")});
                            tmpRuntimeHashMap.put(luaRuntime.getName(), luaRuntime);
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            LuaRuntime luaRuntime = new LuaRuntime(luacType, "root", xpcall, new Path[]{Paths.get(paths)});
            tmpRuntimeHashMap.put(luaRuntime.getName(), luaRuntime);
        }
        HashMap<String, LuaRuntime> tmp = this.runtimeHashMap;
        this.runtimeHashMap = tmpRuntimeHashMap;
        if (tmp != null && !tmp.isEmpty()) {
            DefaultExecutor.getIns().schedule(() -> {
                tmp.values().forEach(v -> v.close());
            }, 30_000, TimeUnit.MILLISECONDS);
        }
    }

    public LuaRuntime getRuntime() {
        return runtimeHashMap.get("root");
    }

    public LuaRuntime getRuntime(String name) {
        return runtimeHashMap.get(name);
    }

    public void set(String key, LuaFunction value) {
        runtimeHashMap.values().forEach(v -> v.getGlobals().put(key, value));
    }

    public void set(String key, Object value) {
        runtimeHashMap.values().forEach(v -> v.getGlobals().put(key, value));
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
        for (LuaRuntime runtime : runtimeHashMap.values()) {
            runtime.close();
        }
    }

}
