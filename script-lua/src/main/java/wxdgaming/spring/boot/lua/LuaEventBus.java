package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.spring.boot.core.io.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lua 脚本 加载器 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-06-27 10:10
 **/
@Slf4j
@Getter
public class LuaEventBus {

    @Getter private static final ConcurrentHashMap<String, Object> lua_data = new ConcurrentHashMap<>();

    /**
     * 规则目录下面
     * --script
     * --------模块1
     * --------模块2
     * --------模块3
     * --util 公共脚本
     *
     * @param base_dir 主目录
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-28 17:33
     */
    public static LuaEventBus buildFromDirs(String base_dir) {
        LuaEventBus luaEventBus = new LuaEventBus();

        File script_path = new File(base_dir + "/script");

        FileUtil.walkDirs(script_path.getPath(), 1).forEach(dir -> {
            if (dir.equals(script_path)) return;
            log.info("load lua module：{} - {}", dir, dir.getName());
            LuaRuntime luaRuntime = new LuaRuntime(dir.getName());
            luaRuntime.loadDir(dir.getPath());
            luaRuntime.loadDir(base_dir + "/util");
            luaRuntime.set("jlog", LuaLogger.getIns());
            luaRuntime.set("globalArgs", lua_data);
            luaEventBus.put(luaRuntime);
        });

        return luaEventBus;
    }

    private LinkedHashMap<String, LuaRuntime> globalPools = new LinkedHashMap<>();

    synchronized void put(LuaRuntime luaRuntime) {
        LinkedHashMap<String, LuaRuntime> stringLuaRuntimeLinkedHashMap = new LinkedHashMap<>(globalPools);
        stringLuaRuntimeLinkedHashMap.put(luaRuntime.getName(), luaRuntime);
        globalPools = stringLuaRuntimeLinkedHashMap;
    }

    public void set(String key, JavaFunction value) {
        globalPools.values().forEach(v -> v.set(key, value));
    }

    public void set(String key, Object value) {
        globalPools.values().forEach(v -> v.set(key, value));
    }

    /** 把一个方法转化成函数传递给lua */
    public void pushJavaFunction(Object bean, Method method) {
        pushJavaFunction(bean, method.getName(), method);
    }

    /** 把一个方法转化成函数传递给lua */
    public void pushJavaFunction(final Object bean, String key, Method method) {
        JavaFunction jFunction = new JavaFunction() {
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

    /** 当前线程上下文 */
    public LuaRuntime contextModule(String key) {
        return globalPools.get(key);
    }

    /** 当前线程上下文 */
    public LuaContext context(String key) {
        for (LuaRuntime luaRuntime : globalPools.values()) {
            LuaContext context = luaRuntime.context();
            if (context.has(key)) {
                return context;
            }
        }
        return null;
    }

    /** 当前线程上下文 */
    public LuaValue find(String key) {
        for (LuaRuntime luaRuntime : globalPools.values()) {
            LuaContext context = luaRuntime.context();
            LuaValue luaValue = context.find(key);
            if (luaValue != null && luaValue.type() != Lua.LuaType.NIL) {
                return luaValue;
            }
        }
        return null;
    }

    /** 自动释放资源 */
    public void call(String key, Object... args) {
        globalPools.values().forEach(globalPool -> {
            try (LuaContext lua = globalPool.newContext()) {
                lua.call(key, args);
            }
        });
    }

}
