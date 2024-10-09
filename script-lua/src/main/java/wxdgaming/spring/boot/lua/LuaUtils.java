package wxdgaming.spring.boot.lua;

import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaTableValue;
import party.iroiro.luajava.value.LuaValue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: 陈大侠
 * @date: 2024/6/27 13:08
 * @desc:
 */
public class LuaUtils {

    public static Object luaValue2Object(LuaValue luaValue) {
        if (luaValue.type() == Lua.LuaType.NUMBER) {
            if(luaValue instanceof LuaLong) {
                long int64 = luaValue.toInteger();
                int int32 = (int) int64;
                if (int64 == int32) {
                    return int32;
                } else {
                    return int64;
                }
            }
            return luaValue.toNumber();
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

    public static void push(Lua L, Object object) {
        if (object != null && object.getClass().isArray()) {
            L.pushJavaArray(object);
        } else {
            L.push(object, Lua.Conversion.FULL);
        }
    }

}
