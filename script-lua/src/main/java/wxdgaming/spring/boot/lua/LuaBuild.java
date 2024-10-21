package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * lua 构建器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 20:52
 **/
@Getter
@Setter
@Configuration
@ConditionalOnProperty("lua")
public class LuaBuild {

    private String path;
    private LuacType luacType = LuacType.LUA54;
    private boolean useModule = false;
    private boolean useXpcall = true;

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "lua.path")
    public LuaService luaService() {
        return LuaService.of(LuacType.LUA54, useModule, useXpcall, path);
    }

}
