package wxdgaming.spring.logserver.module.logs;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.logserver.bean.LogEntity;
import wxdgaming.spring.logserver.bean.LogField;
import wxdgaming.spring.logserver.bean.LogMappingInfo;
import wxdgaming.spring.logserver.bean.LogTableContext;
import wxdgaming.spring.logserver.module.data.DataCenterService;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    final DataCenterService dataCenterService;
    final Map<String, LogTableContext> logTableContextMap = MapOf.newConcurrentHashMap();

    @Autowired
    public LogService(PgsqlDataHelper pgsqlDataHelper, DataCenterService dataCenterService) {
        this.pgsqlDataHelper = pgsqlDataHelper;
        this.dataCenterService = dataCenterService;
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

    public List<JSONObject> nav() {
        return dataCenterService.getLogMappingInfoList().stream()
                .map(li -> {
                    JSONObject jsonObject = MapOf.newJSONObject();
                    jsonObject.put("name", li.getLogName());
                    jsonObject.put("comment", li.getLogComment());
                    return jsonObject;
                })
                .toList();
    }

    public List<JSONObject> logTitle(String tableName) {
        return dataCenterService.getLogMappingInfoList().stream()
                .filter(li -> li.getLogName().equals(tableName))
                .mapMulti(new BiConsumer<LogMappingInfo, Consumer<JSONObject>>() {
                    @Override public void accept(LogMappingInfo li, Consumer<JSONObject> consumer) {
                        {
                            JSONObject jsonObject = MapOf.newJSONObject();
                            jsonObject.put("name", "createTime");
                            jsonObject.put("comment", "时间");
                            consumer.accept(jsonObject);
                        }
                        {
                            JSONObject jsonObject = MapOf.newJSONObject();
                            jsonObject.put("name", "uid");
                            jsonObject.put("comment", "UID");
                            consumer.accept(jsonObject);
                        }
                        List<LogField> fieldList = li.getFieldList();
                        for (LogField logField : fieldList) {
                            JSONObject jsonObject = MapOf.newJSONObject();
                            jsonObject.put("name", logField.getFieldName());
                            jsonObject.put("comment", logField.getFieldComment());
                            consumer.accept(jsonObject);
                        }
                    }
                })
                .toList();
    }

}
