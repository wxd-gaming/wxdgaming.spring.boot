package wxdgaming.spring.boot.weblua.service;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-31 20:39
 **/
@Slf4j
@Service
public class ResponseService implements InitPrint {

    public void response(HttpServletResponse response, String contentType, String text) throws Exception {
        response(response, contentType, text.getBytes(StandardCharsets.UTF_8));
    }

    public void response(HttpServletResponse response, String contentType, byte[] bytes) throws Exception {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(contentType);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(bytes);
            outputStream.flush();
        }
    }

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
        response(
                response,
                MediaType.TEXT_PLAIN.toString(),
                res
        );
    }

    public void responseJson(HttpServletResponse response, String json) throws Exception {
        response(response, MediaType.APPLICATION_JSON.toString(), json);
    }

    public void responseText(HttpServletResponse response, String text) throws Exception {
        response(response, MediaType.TEXT_PLAIN.toString(), text);
    }

    public void responseFile(HttpServletResponse response, String filePath) throws Exception {
        File file = new File(filePath);
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        byte[] bytes = Files.readAllBytes(file.toPath());
        response(response, "application/x-download", bytes);
        log.info("fileName={}", file.getName());
    }

}
