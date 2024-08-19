package code;

import org.junit.Test;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

public class LuaDebugTest {


    @Test
    public void t0() {
        Globals globals = JsePlatform.debugGlobals();
        globals.loadfile("G:\\gitee\\wxdgaming.spring.boot\\script-lua\\src\\main\\lua\\script\\2\\my.lua").call();
        LuaValue debug = globals.get("debug");
        globals.set("time", CoerceJavaToLua.coerce(Long.valueOf(System.currentTimeMillis())));
        globals.load("print(type(os.time()))").call();
        globals.load("print(os.time())").call();
        globals.load("print(type(time))").call();
        globals.load("print(time)").call();
        globals.load("print(tostring(time))").call();
        globals.load("print(tostring(time))").call();

    }


    @Test
    public void t32() {
        System.out.println(FastJsonUtil.toJson(0.42334D));
    }

}
