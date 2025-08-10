package wxdgaming.spring.logserver.module.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.batis.TableMapping;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.logserver.bean.LogEntity;
import wxdgaming.spring.logserver.bean.LogMappingInfo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据中心
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:53
 **/
@Slf4j
@Getter
@Service
public class DataCenterService implements InitPrint {

    final PgsqlDataHelper sqlDataHelper;
    List<LogMappingInfo> logMappingInfoList;

    @Autowired
    public DataCenterService(PgsqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @Start
    public void start() {
        log.info("DataCenterService start");
        initDataHelper();
    }

    public void initDataHelper() {
        Map<String, String> dbTableMap = sqlDataHelper.findTableMap();
        Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = sqlDataHelper.findTableStructMap();

        String json = FileReadUtil.readString("log-init.json", StandardCharsets.UTF_8);
        logMappingInfoList = FastJsonUtil.parseArray(json, LogMappingInfo.class);
        for (LogMappingInfo logMappingInfo : logMappingInfoList) {
            String tableName = logMappingInfo.getLogName();
            String tableComment = logMappingInfo.getLogComment();
            TableMapping tableMapping = sqlDataHelper.tableMapping(LogEntity.class);
            checkSLogTable(sqlDataHelper, dbTableMap, tableStructMap, tableMapping, logMappingInfo.isPartition(), tableName, tableComment);
        }

    }

    public void checkSLogTable(PgsqlDataHelper dataHelper,
                               Map<String, String> dbTableMap,
                               Map<String, LinkedHashMap<String, JSONObject>> tableStructMap,
                               TableMapping tableMapping,
                               boolean checkPartition,
                               String tableName,
                               String tableComment) {

        dataHelper.checkTable(tableStructMap, tableMapping, tableName, tableComment);
        if (checkPartition) {
            /*TODO 处理分区表 */
            LocalDateTime localDate = LocalDateTime.now().plusDays(-2);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                /*创建表分区*/
                String from = MyClock.formatDate("yyyyMMdd", localDate.plusDays(i));
                String to = MyClock.formatDate("yyyyMMdd", localDate.plusDays(i + 1));

                String partition_table_name = tableName + "_" + from;
                if (dbTableMap.containsKey(partition_table_name)) {
                    continue;
                }
                sb.append(dataHelper.buildPartition(tableName, from, to)).append("\n");
            }
            if (!sb.isEmpty()) {
                dataHelper.executeUpdate(sb.toString());
            }
        }
    }
}
