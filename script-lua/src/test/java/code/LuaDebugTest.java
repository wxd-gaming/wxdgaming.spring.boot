package code;

import org.junit.Test;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.lua.LuaRuntime;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LuaDebugTest {


    @Test
    public void t0() {
        LuaRuntime luaRuntime = new LuaRuntime("test", new Path[]{Paths.get("src/test/lua/test.lua")});
    }


    @Test
    public void t32() {
        System.out.println(FastJsonUtil.toJson(0.42334D));
    }

}
