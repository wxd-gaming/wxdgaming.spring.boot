package wxdgaming.spring.boot.web.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

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

    public void responseObject2Json(HttpServletResponse response, Object object) throws Exception {
        response(response, MediaType.APPLICATION_JSON.toString(), FastJsonUtil.toJson(object));
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
