package wxdgaming.game.login.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.spring.boot.batis.sql.SqlDataCache;
import wxdgaming.spring.boot.batis.sql.SqlDataHelper;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.format.HexId;

/**
 * 登录服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:37
 **/
@Slf4j
@Getter
@Service
public class LoginService {

    HexId userHexId;
    final SqlDataHelper sqlDataHelper;

    public LoginService(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
    }

    @Start
    public void start(@Value("${sid}") int sid) {
        userHexId = new HexId(sid);
    }

    public UserData userData(String userName) {
        SqlDataCache<UserData, String> cached = sqlDataHelper.getCacheService().cache(UserData.class);
        return cached.getIfPresent(userName);
    }

}
