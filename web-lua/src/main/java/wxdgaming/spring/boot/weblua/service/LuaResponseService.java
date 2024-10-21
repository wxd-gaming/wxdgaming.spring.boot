package wxdgaming.spring.boot.weblua.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.lua.LuaUtils;
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
        String res = "";
        Object object = obj;
        if (obj instanceof LuaValue) {
            object = LuaUtils.luaValue2Object((LuaValue) obj);
        }
        if (object instanceof Number
                || object instanceof String) {
            res = String.valueOf(object);
        } else {
            res = FastJsonUtil.toJson(object);
        }
        responseService.response(
                response,
                MediaType.TEXT_PLAIN.toString(),
                res
        );
    }

}
