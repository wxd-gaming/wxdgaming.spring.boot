package wxdgaming.spring.boot.lua;

import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

/**
 * JFun
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-26 11:01
 */
public interface LuaFunction extends JFunction {

    @Override default int __call(Lua L) {
        try {
            int oldTop = L.getTop();
            Object[] _args = new Object[oldTop];
            for (int i = 0; i < _args.length; i++) {
                LuaValue luaValue1 = L.get();
                Object javaObject = LuaUtils.luaValue2Object(luaValue1);
                _args[_args.length - i - 1] = javaObject;
            }
            L.setTop(oldTop);
            Object results = doAction(L, _args);
            if (results != null) {
                LuaUtils.push(L, results);
            }
            return results == null ? 0 : 1;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    Object doAction(Lua L, Object[] args);

}
