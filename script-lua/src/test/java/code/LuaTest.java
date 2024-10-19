package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.lua.LuaEventBus;
import wxdgaming.spring.boot.lua.LuaFunction;
import wxdgaming.spring.boot.lua.LuaLogger;

import java.io.File;
import java.util.Arrays;

/**
 * 测试lua
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 12:05
 */
@Slf4j
public class LuaTest {

    @Test
    public void t1() {

        String path = "src/main/lua/";
        File script_path = new File(path + "/script");
        FileUtil.walkDirs(script_path.getPath(), 1).forEach(dir -> {
            if (dir.equals(script_path)) return;
            System.out.println(dir + " - " + dir.getFileName());
        });

        System.out.println("========================================================");

        FileUtil.walkFiles(path + "/util", 99).forEach(file -> {
            System.out.println(file);
        });

    }

    protected static LuaEventBus luaEventBus = null;

    static int forCount = 5000;

    public interface T {
        int t1 = 1;
    }

    @Test
    public void t34() {
        System.out.println(System.currentTimeMillis() + " - " + System.getProperty("user.dir"));
        // luaBus = LuaBus.buildFromResources(Thread.currentThread().getContextClassLoader(), "script/");
        luaEventBus = LuaEventBus.buildFromDirs("src/main/lua");
        luaEventBus.set("objVar", 1);
        luaEventBus.set("jlog", LuaLogger.getIns());
        /*注册函数*/
        luaEventBus.set("testfun0", new LuaFunction() {
            @Override public Object doAction(Lua L, Object[] args) {
                log.info("{}", Arrays.toString(args));
                return "java 返回值";
            }
        });
        luaEventBus.pCall("login");
    }


}
