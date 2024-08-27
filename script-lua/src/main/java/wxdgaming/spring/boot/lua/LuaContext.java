package wxdgaming.spring.boot.lua;

import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;

/**
 * 当前lua上下文
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-23 17:30
 **/
@Slf4j
public class LuaContext implements Closeable {

    private final Lua lua;

    public LuaContext(Lua context) {
        this.lua = context;
    }

    public boolean has(String key) {
        LuaValue luaValue = lua.get(key);
        return has(luaValue);
    }

    public boolean has(LuaValue luaValue) {
        return luaValue != null && luaValue.type() != Lua.LuaType.NIL;
    }

    public LuaValue find(String key) {
        return lua.get(key);
    }

    public LuaValue call(String key, Object... args) {
        LuaValue luaValue = find(key);
        int top = lua.getTop();
        luaValue.push(lua);
        for (Object o : args) {
            if (o instanceof Map<?, ?>) {
                lua.push((Map<?, ?>) o);
            } else if (o instanceof Collection<?>) {
                lua.push((Collection<?>) o);
            } else if (o instanceof Number) {
                lua.push((Number) o);
            } else if (o.getClass().isArray()) {
                lua.pushArray(o);
            } else {
                lua.push(o, Lua.Conversion.SEMI);
            }
        }
        lua.pCall(args.length, Consts.LUA_MULTRET);
        int returnCount = lua.getTop() - top;
        if (returnCount == 0) {
            return null;
        }
        LuaValue[] call = new LuaValue[returnCount];
        for (int i = 0; i < returnCount; i++) {
            call[returnCount - i - 1] = lua.get();
        }
        LuaValue value = call[0];
        if (value.type() == Lua.LuaType.NONE || value.type() == Lua.LuaType.NIL) {
            return null;
        }
        return value;
    }

    @Override public void close() {
        lua.close();
    }
}
