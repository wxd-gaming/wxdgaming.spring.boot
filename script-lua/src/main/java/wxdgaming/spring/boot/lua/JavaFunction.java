package wxdgaming.spring.boot.lua;

import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

/**
 * JFun
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-23 16:50
 **/
public interface JavaFunction extends JFunction {

    @Override default int __call(Lua L) {
        try {
            Object[] _args = new Object[L.getTop()];
            for (int i = 0; i < _args.length; i++) {
                LuaValue luaValue1 = L.get();
                Object javaObject = LuaRuntime.luaValue2Object(luaValue1);
                _args[_args.length - i - 1] = javaObject;
            }
            Object results = doAction(L, _args);
            LuaRuntime.push(L, results);
            return 1;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    Object doAction(Lua L, Object[] args);

}
