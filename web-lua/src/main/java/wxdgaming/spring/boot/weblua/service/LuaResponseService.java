package wxdgaming.spring.boot.weblua.service;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.web.service.ResponseService;

/**
 * lua response
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 08:53
 **/
@Slf4j
@Service
public class LuaResponseService {

    @Autowired ResponseService responseService;

    public void responseObj(HttpServletResponse response, Object obj) throws Exception {
        String res;
        if (obj instanceof LuaTable luaTable) {
            JSONObject jsonObject = new JSONObject(true);
            LuaValue[] keys = luaTable.keys();
            for (LuaValue key : keys) {
                jsonObject.put(key.toString(), CoerceLuaToJava.coerce(luaTable.get(key), String.class));
            }
            res = jsonObject.toJSONString();
        } else if (obj instanceof LuaValue luaValue) {
            res = (String) CoerceLuaToJava.coerce(luaValue, String.class);
        } else {
            res = String.valueOf(obj);
        }
        responseService.response(
                response,
                MediaType.TEXT_PLAIN.toString(),
                res
        );
    }

}
