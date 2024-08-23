package code;

import org.junit.Test;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.lua.LuaRuntime;

public class LuaDebugTest {


    @Test
    public void t0() {
        LuaRuntime luaRuntime = new LuaRuntime("test");
        // LuaJC.install(globals);
        luaRuntime.loadfile("src/test/lua/test.lua");
    }


    @Test
    public void t32() {
        System.out.println(FastJsonUtil.toJson(0.42334D));
    }

}
