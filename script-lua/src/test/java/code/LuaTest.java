package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.lua.LuaFunction;
import wxdgaming.spring.boot.lua.LuaScan;
import wxdgaming.spring.boot.lua.LuaService;
import wxdgaming.spring.boot.lua.LuacType;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * 测试lua
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 12:05
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CoreScan.class, DataRedisScan.class, LuaScan.class})
public class LuaTest {

    @Autowired SpringUtil springUtil;

    @Test
    public void t1() {

        String path = "src/main/lua/";
        Path script_path = Path.of(path + "/script");
        FileUtil.walkDirs(script_path, 1).forEach(dir -> {
            if (dir.equals(script_path)) return;
            System.out.println(dir + " - " + dir.getFileName());
        });

        System.out.println("========================================================");

        FileUtil.walkFiles(path + "/util", 99).forEach(file -> {
            System.out.println(file);
        });

    }

    protected static LuaService luaService = null;

    static int forCount = 5000;

    public interface T {
        int t1 = 1;
    }

    @Test
    public void t34() {
        System.out.println(System.currentTimeMillis() + " - " + System.getProperty("user.dir"));
        // luaBus = LuaBus.buildFromResources(Thread.currentThread().getContextClassLoader(), "script/");
        luaService = LuaService.of(LuacType.LUA54, true, "src/main/lua");
        luaService.set("objVar", 1);
        /*注册函数*/
        luaService.set("testfun0", new LuaFunction() {
            @Override public Object doAction(Lua L, Object[] args) {
                log.info("{}", Arrays.toString(args));
                return "java 返回值";
            }
        });
        luaService.getRuntime().call("login");
    }


}
