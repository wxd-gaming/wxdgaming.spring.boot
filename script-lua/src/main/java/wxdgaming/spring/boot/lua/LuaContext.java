package wxdgaming.spring.boot.lua;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

import java.io.Closeable;

/**
 * 当前lua上下文
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-23 17:30
 **/
@Slf4j
public class LuaContext implements Closeable {

    private final Lua context;

    public LuaContext(Lua context) {
        this.context = context;
    }

    public boolean has(String key) {
        LuaValue luaValue = context.get(key);
        return has(luaValue);
    }

    public boolean has(LuaValue luaValue) {
        return luaValue != null && luaValue.type() != Lua.LuaType.NIL;
    }

    public LuaValue find(String key) {
        return context.get(key);
    }

    public Object call(String key, Object... args) {
        LuaValue luaValue = find(key);
        if (!has(luaValue)) return null;
        LuaValue[] call = luaValue.call(args);
        if (call.length == 0) {
            return null;
        }
        return call[0].toJavaObject();
    }

    @Override public void close() {
        context.close();
    }
}
