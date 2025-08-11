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
import wxdgaming.spring.boot.core.executor.ExecutorWith;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.core.lang.Tuple2;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;
import wxdgaming.spring.logserver.bean.LogEntity;
import wxdgaming.spring.logserver.bean.LogMappingInfo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    List<LogMappingInfo> logMappingInfoList = List.of();

    @Autowired
    public DataCenterService(PgsqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @Start
    public void start() {
        log.info("DataCenterService start");
        initLogTable();
    }

    @ExecutorWith(useVirtualThread = true)
    @Scheduled(value = "0 0 0 * * ?", async = true)
    public void initLogTable() {
        Map<String, String> dbTableMap = sqlDataHelper.findTableMap();
        Map<String, LinkedHashMap<String, JSONObject>> tableStructMap = sqlDataHelper.findTableStructMap();

        List<LogMappingInfo> tmp = new ArrayList<>();
        Stream<Tuple2<Path, byte[]>> tuple2Stream = FileUtil.resourceStreams(this.getClass().getClassLoader(), "log-init", ".json");
        tuple2Stream.forEach(tuple2 -> {
            String json = new String(tuple2.getRight(), StandardCharsets.UTF_8);
            LogMappingInfo logMappingInfo = FastJsonUtil.parse(json, LogMappingInfo.class);
            String tableName = logMappingInfo.getLogName();
            String tableComment = logMappingInfo.getLogComment();
            TableMapping tableMapping = sqlDataHelper.tableMapping(LogEntity.class);
            checkSLogTable(sqlDataHelper, dbTableMap, tableStructMap, tableMapping, logMappingInfo.isPartition(), tableName, tableComment);
            tmp.add(logMappingInfo);
        });
        logMappingInfoList = tmp;
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
