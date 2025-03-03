package code.mysql;

import com.alibaba.fastjson.JSONObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import wxdgaming.spring.boot.data.EntityBase;
import wxdgaming.spring.boot.data.converter.ObjectToJsonStringConverter;

/**
 * test1
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-31 09:45
 **/
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(indexes = {
        @Index(columnList = "logType"),
})
public class MysqlLogTest extends EntityBase<Long> {

    private String logType;
    private String name;
    @Convert(converter = ObjectToJsonStringConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private JSONObject sensors = new JSONObject();

    @Override public MysqlLogTest setCreatedTime(Long createdTime) {
        super.setCreatedTime(createdTime);
        return this;
    }

    @Override public MysqlLogTest setUid(Long uid) {
        super.setUid(uid);
        return this;
    }

}
