package wxdgaming.spring.logserver.module.logs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.batis.sql.SqlQueryBuilder;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.core.util.NumberUtil;
import wxdgaming.spring.logserver.bean.LogEntity;
import wxdgaming.spring.logserver.bean.LogField;
import wxdgaming.spring.logserver.bean.LogMappingInfo;
import wxdgaming.spring.logserver.bean.LogTableContext;
import wxdgaming.spring.logserver.module.data.DataCenterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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
        return dataCenterService.getLogMappingInfoMap().values().stream()
                .map(li -> {
                    JSONObject jsonObject = MapOf.newJSONObject();
                    jsonObject.put("name", li.getLogName());
                    jsonObject.put("comment", li.getLogComment());
                    return jsonObject;
                })
                .toList();
    }

    public List<JSONObject> logTitle(String tableName) {
        return dataCenterService.getLogMappingInfoMap().values().stream()
                .filter(li -> li.getLogName().equals(tableName))
                .mapMulti(new BiConsumer<LogMappingInfo, Consumer<JSONObject>>() {
                    @Override public void accept(LogMappingInfo li, Consumer<JSONObject> consumer) {
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

    public RunResult logPage(String tableName,
                             int pageIndex, int pageSize,
                             String minDay, String maxDay, String whereJson) {
        SqlQueryBuilder queryBuilder = pgsqlDataHelper.queryBuilder();
        queryBuilder.setTableName(tableName);

        if (StringUtils.isNotBlank(minDay)) {
            queryBuilder.pushWhereByValueNotNull("daykey>=?", NumberUtil.retainNumber(minDay));
        }

        if (StringUtils.isNotBlank(maxDay)) {
            queryBuilder.pushWhereByValueNotNull("daykey<=?", NumberUtil.retainNumber(maxDay));
        }

        LogMappingInfo logMappingInfo = dataCenterService.getLogMappingInfoMap().get(tableName);
        if (StringUtils.isNotBlank(whereJson)) {
            List<JSONObject> jsonObjects = JSON.parseArray(whereJson, JSONObject.class);
            for (JSONObject jsonObject : jsonObjects) {
                String where = jsonObject.getString("where");
                Function<String, Object> stringObjectFunction = logMappingInfo.fieldValueFunction(where);
                String and = jsonObject.getString("and");
                String format;
                if ("uid".equals(where) || "createTime".equals(where)) {
                    format = "%s " + and + " ?";
                } else if ("<=".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) <= ?";
                } else if ("<".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) < ?";
                } else if (">=".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) >= ?";
                } else if (">".equals(and)) {
                    format = "CAST(logdata::jsonb->>'%s' AS numeric) > ?";
                } else {
                    format = "logdata::jsonb @> jsonb_build_object('%s',?)";
                }
                Object whereValue = stringObjectFunction.apply(jsonObject.getString("whereValue"));
                queryBuilder.pushWhere(format.formatted(where), whereValue);
            }
        }

        queryBuilder.page(pageIndex, pageSize, 1, 1000);
        queryBuilder.setOrderBy("createtime desc");
        System.out.println(queryBuilder.buildSelectSql());
        long rowCount = queryBuilder.findCount();
        List<LogEntity> logEntities = queryBuilder.findList2Entity(LogEntity.class);
        List<JSONObject> list = new ArrayList<>();
        for (LogEntity logEntity : logEntities) {
            JSONObject jsonObject = new JSONObject(logEntity.getLogData());
            jsonObject.put("uid", logEntity.getUid());
            jsonObject.put("createTime", MyClock.formatDate("yyyy-MM-dd HH:mm:ss.SSS", logEntity.getCreateTime()));
            list.add(jsonObject);
        }
        return RunResult.ok().fluentPut("rowCount", rowCount).data(list);
    }

}
