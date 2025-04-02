package wxdgaming.game.test.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 账号主键id
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-02 13:39
 **/
@Getter
@Setter
@Embeddable
@NoArgsConstructor()
@AllArgsConstructor()
public class AccountKey implements Serializable {

    @Serial private static final long serialVersionUID = 1l;

    private long id;
    private int sid;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AccountKey that = (AccountKey) o;
        return getId() == that.getId() && getSid() == that.getSid();
    }

    @Override public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + getSid();
        return result;
    }

    @Override public String toString() {
        return "AccountKey{id=%d, sid=%d}".formatted(id, sid);
    }
}
