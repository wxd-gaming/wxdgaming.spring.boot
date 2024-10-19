package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.spring.boot.core.function.Consumer2;
import wxdgaming.spring.boot.core.io.FileUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            log.info("load lua module：{} - {}", dir, dir.getFileName());
            LuaRuntime luaRuntime = new LuaRuntime(dir.getFileName().toString(), new Path[]{dir, Paths.get(base_dir + "/util")});
            luaRuntime.getGlobals().put("jlog", LuaLogger.getIns());
            luaRuntime.getGlobals().put("globalArgs", lua_data);
            luaEventBus.put(luaRuntime);
        });

        return luaEventBus;
    }

    private LinkedHashMap<String, LuaRuntime> luaRuntimeMap = new LinkedHashMap<>();

    public LuaEventBus() {

    }

    synchronized void put(LuaRuntime luaRuntime) {
        LinkedHashMap<String, LuaRuntime> stringLuaRuntimeLinkedHashMap = new LinkedHashMap<>(luaRuntimeMap);
        stringLuaRuntimeLinkedHashMap.put(luaRuntime.getName(), luaRuntime);
        luaRuntimeMap = stringLuaRuntimeLinkedHashMap;
    }

    public void set(String key, LuaFunction value) {
        luaRuntimeMap.values().forEach(v -> v.getGlobals().put(key, value));
    }

    public void set(String key, Object value) {
        luaRuntimeMap.values().forEach(v -> v.getGlobals().put(key, value));
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

    /** 当前线程上下文 */
    public LuaRuntime contextModule(String key) {
        return luaRuntimeMap.get(key);
    }

    /** 当前线程上下文 */
    public void context(String key, Consumer2<LuaContext, LuaValue> func) {
        for (LuaRuntime luaRuntime : luaRuntimeMap.values()) {
            LuaContext context = luaRuntime.context();
            LuaValue value = context.find(key);
            if (context.has(value)) {
                func.accept(context, value);
            }
        }
    }

    /** 自动释放资源 */
    public void pCall(String key, Object... args) {
        context(key, (context, value) -> context.pCall(value, args));
    }

}
