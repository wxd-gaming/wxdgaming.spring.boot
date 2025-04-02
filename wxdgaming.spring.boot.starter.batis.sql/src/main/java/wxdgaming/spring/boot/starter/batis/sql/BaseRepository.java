package wxdgaming.spring.boot.starter.batis.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础JpaRepository
 *
 * @param <T>
 * @param <ID>
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 20:05
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    default void  test(){
        this.deleteAll();
    }

}
