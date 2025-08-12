package wxdgaming.game.server.module.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.game.server.bean.BackendConfig;
import wxdgaming.game.server.bean.global.DataBase;
import wxdgaming.game.server.bean.global.GlobalDataEntity;
import wxdgaming.game.server.bean.global.GlobalDataType;
import wxdgaming.spring.boot.batis.sql.SqlDataHelper;
import wxdgaming.spring.boot.batis.sql.mysql.MysqlDataHelper;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.ann.Named;
import wxdgaming.spring.boot.core.ann.Shutdown;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentTable;

import java.util.List;

/**
 * 本服的全局数据服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 11:05
 **/
@Slf4j
@Getter
@Service
public class GlobalDataService extends HoldRunApplication {

    private final int sid;
    private final SqlDataHelper sqlDataHelper;
    private final BackendConfig backendConfig;
    /** key: sid, key: type, value: 数据 */
    private final ConcurrentTable<Integer, GlobalDataType, GlobalDataEntity> globalDataTable = new ConcurrentTable<>();

    public GlobalDataService(@Named("sid") int sid, MysqlDataHelper mysqlDataHelper, BackendConfig backendConfig) {
        this.sid = sid;
        this.sqlDataHelper = mysqlDataHelper;
        this.backendConfig = backendConfig;
    }

    @Start
    public void start(@Named("serverType") int serverType) {
        List<GlobalDataEntity> list = this.sqlDataHelper.findListByWhere(GlobalDataEntity.class, "merge = ?", false);
        for (GlobalDataEntity entity : list) {
            GlobalDataType globalDataType = GlobalDataType.ofOrException(entity.getId());
            globalDataTable.put(entity.getSid(), globalDataType, entity);
        }
    }

    @Order(100)
    @Shutdown
    public void close() {
        globalDataTable.forEach(globalDataEntity -> {
            sqlDataHelper.dataBatch().save(globalDataEntity);
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends DataBase> T get(GlobalDataType type) {
        DataBase data = globalDataTable.computeIfAbsent(
                sid,
                type,
                l ->
                        new GlobalDataEntity()
                                .setId(type.getCode())
                                .setSid(sid)
                                .setData(type.getFactory().get())
        ).getData();
        return (T) data;
    }

    public GlobalDataEntity get(int sid, GlobalDataType type) {
        return globalDataTable.get(sid, type);
    }

    public void save(GlobalDataEntity globalDataEntity) {
        sqlDataHelper.save(globalDataEntity);
    }

}
