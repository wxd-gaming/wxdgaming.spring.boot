package wxdgaming.spring.logcenter.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.batis.ColumnType;
import wxdgaming.spring.boot.batis.Entity;
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
public class LogEntity extends Entity implements Serializable, EntityName {

    @Serial private static final long serialVersionUID = 1L;
    /** 归属于那一天 因为联合主键，索引是最左原则 */
    @Partition
    @JSONField(ordinal = 1)
    @DbColumn(key = true, comment = "分区信息")
    private int dayKey;
    @DbColumn(key = true)
    @JSONField(ordinal = 2)
    private long uid = 0;
    /** 具体的日期 */
    @JSONField(ordinal = 3)
    @DbColumn(index = true)
    private long createTime;
    @JSONField(ordinal = 4)
    @DbColumn(ignore = true)
    private String logType;

    @JSONField(ordinal = 99)
    @DbColumn(columnType = ColumnType.Jsonb, index = true)
    private final JSONObject json = MapOf.newJSONObject();

    @Override public String tableName() {
        return logType;
    }

    public void checkDataKey() {
        if (getCreateTime() == 0) {
            setCreateTime(System.currentTimeMillis());
        }

        LocalDate localDate = MyClock.localDate(getCreateTime());
        setDayKey(localDate.getYear() * 10000 + localDate.getMonthValue() * 100 + localDate.getDayOfMonth());
    }

    @Override public final boolean equals(Object o) {
        if (!(o instanceof LogEntity logEntity)) return false;

        return getDayKey() == logEntity.getDayKey() && getUid() == logEntity.getUid();
    }

    @Override public int hashCode() {
        int result = getDayKey();
        result = 31 * result + Long.hashCode(getUid());
        return result;
    }

    public LogEntity putJson(String key, Object value) {
        json.put(key, value);
        return this;
    }

}
