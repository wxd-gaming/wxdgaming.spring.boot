package wxdgaming.spring.boot.data.batis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID> {

    @Query
    T queryAll();

}
