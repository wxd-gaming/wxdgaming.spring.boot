package code;

import code.entity.TestIndex;
import org.springframework.stereotype.Repository;
import wxdgaming.spring.boot.data.batis.BaseRepository;

/**
 * 存储
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-01-13 15:29
 **/
@Repository
public interface TestRepository extends BaseRepository<TestIndex, Long> {

}
