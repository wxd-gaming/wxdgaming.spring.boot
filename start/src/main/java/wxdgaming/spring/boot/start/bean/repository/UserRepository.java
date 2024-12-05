package wxdgaming.spring.boot.start.bean.repository;

import org.springframework.stereotype.Repository;
import wxdgaming.spring.boot.data.batis.BaseRepository;
import wxdgaming.spring.boot.start.bean.entity.User;

/**
 * 用户
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 20:04
 **/
@Repository
public interface UserRepository extends BaseRepository<User, Long> {



}
