package wxdgaming.spring.logserver.module.logs.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.zip.GzipUtil;
import wxdgaming.spring.logserver.bean.LogEntity;
import wxdgaming.spring.logserver.module.logs.LogService;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 日志接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:05
 **/
@Slf4j
@RestController
@RequestMapping("/api/log")
public class LogPushController {

    final LogService logService;

    @Autowired
    public LogPushController(LogService logService) {
        this.logService = logService;
    }

    @RequestMapping("/pushList")
    public RunResult pushList(HttpServletRequest request, @RequestBody byte[] bytes) {
        if (bytes == null || bytes.length < 1) {
            return RunResult.fail("logEntityList 不能为空");
        }
        String json = null;
        String header = request.getHeader(HttpHeaderNames.CONTENT_ENCODING.toString());
        if (header != null && header.equalsIgnoreCase("gzip")) {
            json = GzipUtil.unGzip2String(bytes);
        } else {
            json = new String(bytes, StandardCharsets.UTF_8);
        }

        List<LogEntity> logEntityList = JSON.parseArray(json, LogEntity.class);

        String authorization = request.getHeader(HttpHeaderNames.AUTHORIZATION.toString());
        String jsonString = JSON.toJSONString(logEntityList, SerializerFeature.SortField, SerializerFeature.MapSortField);

        for (LogEntity logEntity : logEntityList) {
            if (StringUtils.isBlank(logEntity.getLogType())) {
                return RunResult.fail("logType 不能为空");
            }
            logService.submitLog(logEntity);
        }
        return RunResult.ok();
    }

    @RequestMapping("/push")
    public RunResult push(HttpServletRequest request, @RequestBody LogEntity logEntity) {
        if (StringUtils.isBlank(logEntity.getLogType())) {
            return RunResult.fail("logType 不能为空");
        }

        String authorization = request.getHeader(HttpHeaderNames.AUTHORIZATION.toString());
        String jsonString = JSON.toJSONString(logEntity, SerializerFeature.SortField, SerializerFeature.MapSortField);

        logService.submitLog(logEntity);
        return RunResult.ok();
    }

}
