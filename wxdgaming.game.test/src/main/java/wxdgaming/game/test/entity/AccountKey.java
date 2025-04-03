package wxdgaming.game.test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

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

    private int sid;
    @Column(length = 64)
    private String account;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        AccountKey that = (AccountKey) o;
        return getSid() == that.getSid() && Objects.equals(getAccount(), that.getAccount());
    }

    @Override public int hashCode() {
        int result = getSid();
        result = 31 * result + Objects.hashCode(getAccount());
        return result;
    }

    @Override public String toString() {
        return "AccountKey{sid=%d, account='%s'}".formatted(sid, account);
    }
}
