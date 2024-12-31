package wxdgaming.spring.boot.weblua.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.lua.LuaService;

import java.io.IOException;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-31 20:18
 **/
@Slf4j
@Controller
@ConditionalOnProperty("lua")
public class LuaApiController implements InitPrint {

    final LuaService luaService;

    final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public LuaApiController(@Qualifier("redisTemplate") RedisTemplate redisTemplate, LuaService luaService) {
        this.redisTemplate = redisTemplate;
        this.luaService = luaService;
    }

    @ResponseBody
    @RequestMapping("/lua/reload")
    public String reload() throws IOException {
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

        Thread x = Thread.currentThread();
        System.out.println(x + " - " + x.isVirtual());

        Object obj = luaService.getLuaRuntime().call(servletPath, request, response, body);

    }

}
