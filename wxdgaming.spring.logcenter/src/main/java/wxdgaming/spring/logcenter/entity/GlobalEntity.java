package wxdgaming.spring.logcenter.entity;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.batis.ColumnType;
import wxdgaming.spring.boot.batis.EntityIntegerUID;
import wxdgaming.spring.boot.batis.ann.DbColumn;
import wxdgaming.spring.boot.batis.ann.DbTable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局数据表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:56
 **/
@Getter
@Setter
@DbTable
public class GlobalEntity extends EntityIntegerUID {

    private long updateTime;
    @DbColumn(columnType = ColumnType.String, length = 50000)
    private ConcurrentHashMap<String, Object> jsonMap = new ConcurrentHashMap<>();

}
