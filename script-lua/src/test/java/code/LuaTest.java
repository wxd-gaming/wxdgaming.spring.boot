package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.lua.LuaScan;
import wxdgaming.spring.boot.lua.LuaService;
import wxdgaming.spring.boot.lua.bean.LuaActor;

/**
 * 测试lua
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 12:05
 */
@Slf4j
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootApplication
@SpringBootTest(classes = {CoreScan.class, DataRedisScan.class, LuaScan.class, LuaSpringReflect.class})
public class LuaTest {

    @Autowired LuaService luaService;
    @Autowired LuaSpringReflect luaReflect;

    @Before
    public void before() {
        luaReflect.content().executorAppStartMethod();
    }

    @Test
    public void execute() {
        luaService.getLuaRuntime().call("onlogin", new LuaActor(9527L, "9527"));
    }


}
