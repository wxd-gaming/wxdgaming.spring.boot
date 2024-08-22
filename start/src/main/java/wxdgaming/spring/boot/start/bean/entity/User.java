package wxdgaming.spring.boot.start.bean.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 20:02
 **/
@Getter
@Setter
@Accessors(chain = true)
@Table
@Entity
public class User {

    @Id
    @Column()
    private long uid;
    @Column()
    private String userName;


}
