package wxdgaming.spring.logserver.module.logs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.logserver.bean.LogEntity;
import wxdgaming.spring.logserver.bean.LogTableContext;

import java.util.Map;

/**
 * 日志服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 17:04
 **/
@Slf4j
@Service
public class LogService implements InitPrint {

    final PgsqlDataHelper pgsqlDataHelper;
    final Map<String, LogTableContext> logTableContextMap = MapOf.newConcurrentHashMap();

    @Autowired
    public LogService(PgsqlDataHelper pgsqlDataHelper) {
        this.pgsqlDataHelper = pgsqlDataHelper;
    }

    public LogTableContext logTableContext(String logName) {
        return logTableContextMap.computeIfAbsent(logName, LogTableContext::new);
    }

    public void submitLog(LogEntity logEntity) {
        LogTableContext logTableContext = logTableContext(logEntity.getLogType());
        if (logEntity.getUid() == 0) {
            log.debug("uid 为0 {}", logEntity);
            logEntity.setUid(logTableContext.newId());
        }
        if (logTableContext.filter(logEntity.getUid())) {
            log.debug("uid 已存在丢弃 {}", logEntity);
            return;
        }
        logEntity.checkDataKey();
        logTableContext.addFilter(logEntity.getUid());
        log.debug("保存 uid={}, logType={}, entity={}", logEntity.getUid(), logEntity.getLogType(), logEntity);
        pgsqlDataHelper.getDataBatch().insert(logEntity);
    }

}
