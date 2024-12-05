package wxdgaming.spring.boot.data.batis;

import org.springframework.data.mongodb.repository.MongoRepository;
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
public interface BaseRepository<T, ID> extends MongoRepository<T, ID> {

}
