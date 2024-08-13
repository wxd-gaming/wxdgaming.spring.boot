package wxdgaming.spring.boot.weblua.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.LuaValue;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.weblua.service.LuaService;

import java.io.IOException;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-31 20:18
 **/
@Slf4j
@Controller
public class LuaApiController implements InitPrint {

    final LuaService luaService;

    final RedisTemplate<?, ?> redisTemplate;

    public LuaApiController(RedisTemplate<?, ?> redisTemplate, LuaService luaService) {
        this.redisTemplate = redisTemplate;
        this.luaService = luaService;
    }

    @ResponseBody
    @RequestMapping("/lua/reload")
    public String reload() throws IOException {
        luaService._initPrint();
        return "ok";
    }

    @RequestMapping("/lua/**")
    public void all(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody(required = false) String body) {
        String servletPath = request.getServletPath();
        int index = Math.min(5, servletPath.length());
        int len = servletPath.length();
        if (servletPath.endsWith("/")) {
            len = servletPath.length() - 1;
        }
        servletPath = servletPath.substring(index, len);
        servletPath = servletPath.replace("/", "_");

        LuaValue[] luaValues = luaService.parse(request, response, body);
        LuaValue lua_func = luaService.get(servletPath);
        if (lua_func == null || lua_func == LuaValue.NIL) {
            lua_func = luaService.get("root");
        }
        lua_func.invoke(luaValues);
    }

}
