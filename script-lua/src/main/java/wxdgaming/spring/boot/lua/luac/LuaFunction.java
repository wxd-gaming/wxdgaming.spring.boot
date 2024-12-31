package wxdgaming.spring.boot.lua.luac;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import party.iroiro.luajava.JFunction;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * JFun
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-26 11:01
 */
@Slf4j
@Component
public abstract class LuaFunction implements JFunction {

    @Override public int __call(Lua L) {
        List<Object> _args = null;
        try {
            int oldTop = L.getTop();
            _args = new ArrayList<>(oldTop);
            for (int i = 0; i < oldTop; i++) {
                LuaValue luaValue1 = L.get();
                Object javaObject = LuaUtils.luaValue2Object(luaValue1);
                _args.addFirst(javaObject);
            }
            L.setTop(oldTop);
            Object results = doAction(L, _args);
            if (results != null) {
                LuaUtils.push(L, results);
            }
            return results == null ? 0 : 1;
        } catch (Throwable e) {
            String jsonString = "";
            try {
                jsonString = JSON.toJSONString(_args);
            } catch (Exception ignore) {}
            log.error("call lua function error {}", jsonString, e);
            throw new RuntimeException("call lua function error " + jsonString, e);
        }
    }

    protected abstract Object doAction(Lua L, List<Object> args);

}
