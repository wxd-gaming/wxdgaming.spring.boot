package wxdgaming.spring.logserver.module.logs.spi;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.logserver.module.logs.LogService;

import java.util.List;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-09 18:06
 **/
@Slf4j
@RestController
@RequestMapping("/api/log")
public class LogFindController implements InitPrint {

    final LogService logService;

    public LogFindController(LogService logService) {
        this.logService = logService;
    }

    @ResponseBody
    @RequestMapping("/nav")
    public ResponseEntity<RunResult> nav() {
        List<JSONObject> nav = logService.nav();
        return ResponseEntity.ok(RunResult.ok().data(nav));
    }

    @ResponseBody
    @RequestMapping("/logTitle")
    public ResponseEntity<RunResult> logTitle(@RequestParam("tableName") String tableName) {
        List<JSONObject> list = logService.logTitle(tableName);
        return ResponseEntity.ok(RunResult.ok().data(list));
    }

    @ResponseBody
    @RequestMapping("/logPage")
    public ResponseEntity<RunResult> logPage(
            @RequestParam("tableName") String tableName,
            @RequestParam("pageIndex") int pageIndex,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("minDay") String minDay,
            @RequestParam("maxDay") String maxDay,
            @RequestParam("where") String where
    ) {
        return ResponseEntity.ok(logService.logPage(tableName, pageIndex, pageSize, minDay, maxDay, where));
    }

}
