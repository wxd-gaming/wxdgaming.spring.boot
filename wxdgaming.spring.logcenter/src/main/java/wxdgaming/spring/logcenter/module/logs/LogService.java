package wxdgaming.spring.logcenter.module.logs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.cache2.CASCache;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.logcenter.bean.LogEntity;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    final Map<String, CASCache<Long, Boolean>> logIdFilterMap = MapOf.newConcurrentHashMap();

    @Autowired
    public LogService(PgsqlDataHelper pgsqlDataHelper) {
        this.pgsqlDataHelper = pgsqlDataHelper;
    }

    public CASCache<Long, Boolean> logFilter(String logName) {
        return logIdFilterMap.computeIfAbsent(logName, l -> {
            CASCache<Long, Boolean> build = CASCache.<Long, Boolean>builder()
                    .area(10)
                    .expireAfterWriteMs(TimeUnit.HOURS.toMillis(24))
                    .build();
            build.start();
            return build;
        });
    }

    public void submitLog(LogEntity logEntity) {
        CASCache<Long, Boolean> longBooleanCASCache = logFilter(logEntity.getLogType());
        if (longBooleanCASCache.has(logEntity.getUid())) {
            log.debug("uid 已存在丢弃 {}", logEntity);
            return;
        }
        logEntity.checkDataKey();
        longBooleanCASCache.put(logEntity.getUid(), true);
        log.debug("保存 {}", logEntity);
        pgsqlDataHelper.getDataBatch().insert(logEntity);
    }

}
