package wxdgaming.spring.logcenter.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.batis.ColumnType;
import wxdgaming.spring.boot.batis.EntityLongUID;
import wxdgaming.spring.boot.batis.EntityName;
import wxdgaming.spring.boot.batis.ann.DbColumn;
import wxdgaming.spring.boot.batis.ann.DbTable;
import wxdgaming.spring.boot.batis.sql.ann.Partition;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.core.timer.MyClock;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:43
 **/
@Getter
@Setter
@DbTable
public class LogEntity extends EntityLongUID implements Serializable, EntityName {

    @Serial private static final long serialVersionUID = 1L;
    /** 归属于那一天 */
    @Partition
    @JSONField(ordinal = 1)
    @DbColumn(key = true, comment = "分区信息")
    private int dayKey;
    /** 具体的日期 */
    @JSONField(ordinal = 2)
    @DbColumn(index = true)
    private long createTime;
    @JSONField(ordinal = -1)
    @DbColumn(ignore = true)
    private String logType;

    @JSONField(ordinal = 99)
    @DbColumn(columnType = ColumnType.Json)
    private final JSONObject json = MapOf.newJSONObject();

    public void checkDataKey() {
        if (getCreateTime() == 0) {
            setCreateTime(System.currentTimeMillis());
        }

        LocalDate localDate = MyClock.localDate(getCreateTime());
        setDayKey(localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth());
    }

    @Override public String tableName() {
        return logType;
    }
}
