package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaTableValue;
import party.iroiro.luajava.value.LuaValue;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
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

    public static Object luaValue2Object(LuaValue luaValue) {
        if (luaValue.type() == Lua.LuaType.NUMBER) {
            long integer = luaValue.toInteger();
            if (integer == (int) integer) {
                return (int) integer;
            }
            double number = luaValue.toNumber();
            if (integer == number) {
                return integer;
            }
            return number;

        } else if (luaValue.type() == Lua.LuaType.TABLE) {
            LuaTableValue luaTableValue = (LuaTableValue) luaValue;
            Map<Object, Object> map = new HashMap<>();
            for (Map.Entry<LuaValue, LuaValue> entry : luaTableValue.entrySet()) {
                map.put(luaValue2Object(entry.getKey()), luaValue2Object(entry.getValue()));
            }
            return map;
        } else if (luaValue.type() == Lua.LuaType.NONE || luaValue.type() == Lua.LuaType.NIL) {
            return null;
        }
        return luaValue.toJavaObject();
    }

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
        return threadLocal.computeIfAbsent(Thread.currentThread(), k -> newContext());
    }

    /** 关闭资源 */
    @Override public void close() {
        threadLocal.values().forEach(LuaContext::close);
        threadLocal = null;
    }
}
