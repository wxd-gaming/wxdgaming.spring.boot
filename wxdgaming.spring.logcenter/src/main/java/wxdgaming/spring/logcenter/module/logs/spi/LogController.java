package wxdgaming.spring.logcenter.module.logs.spi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.logcenter.bean.LogEntity;
import wxdgaming.spring.logcenter.module.logs.LogService;

/**
 * 日志接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:05
 **/
@Slf4j
@RestController
@RequestMapping("/api/log")
public class LogController {

    final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
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
