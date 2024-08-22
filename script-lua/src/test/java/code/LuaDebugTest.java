package code;

import org.junit.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

public class LuaDebugTest {


    @Test
    public void t0() {
        Globals globals = JsePlatform.debugGlobals();
        // LuaJC.install(globals);
        globals.loadfile("src/test/lua/test.lua").call();


    }


    @Test
    public void t32() {
        System.out.println(FastJsonUtil.toJson(0.42334D));
    }

}
