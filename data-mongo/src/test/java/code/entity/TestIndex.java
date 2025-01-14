package code.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 测试索引聚合
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-01-13 15:25
 **/
@Getter
@Setter
@Document()
@CompoundIndex
public class TestIndex {
    @Id
    private long id;
    @Indexed
    private int index;
    @Indexed
    private String username;
    private String email;
}
