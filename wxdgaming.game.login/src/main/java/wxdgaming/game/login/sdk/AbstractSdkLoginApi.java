package wxdgaming.game.login.sdk;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.game.login.AppPlatformParams;
import wxdgaming.game.login.LoginConfig;
import wxdgaming.game.login.bean.UserData;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.game.login.service.LoginService;
import wxdgaming.spring.boot.batis.sql.SqlDataHelper;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.token.JsonTokenBuilder;
import wxdgaming.spring.boot.core.util.SingletonLockUtil;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 渠道参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:22
 **/
public abstract class AbstractSdkLoginApi {

    @Autowired protected LoginService loginService;
    @Autowired protected SqlDataHelper sqlDataHelper;
    @Autowired protected LoginConfig loginConfig;
    @Autowired protected InnerService innerService;

    /** 平台 */
    public abstract AppPlatformParams.Platform platform();

    public abstract RunResult login(HttpServletRequest request, AppPlatformParams appPlatformParams);

    protected UserData createUserData(String account, AppPlatformParams appPlatformParams, String platformUserId) {
        UserData userData = new UserData();
        userData.setAccount(account);
        userData.setAppId(appPlatformParams.getAppId());
        userData.setPlatform(appPlatformParams.getPlatform().name());
        userData.setPlatformUserId(platformUserId);
        userData.setCreateTime(System.currentTimeMillis());
        return userData;
    }

    public UserData getUserData(String account, Supplier<UserData> supplier) {
        SingletonLockUtil.lock(account);
        try {
            UserData userData = loginService.userData(account);
            if (userData == null) {
                userData = supplier.get();
                sqlDataHelper.getCacheService().cache(UserData.class).put(userData.getAccount(), userData);
            }
            return userData;
        } finally {
            SingletonLockUtil.unlock(account);
        }
    }

    public RunResult buildResult(UserData userData) {
        JsonTokenBuilder jwtBuilder = JsonTokenBuilder.of(loginConfig.getJwtKey(), TimeUnit.MINUTES, 5);
        jwtBuilder.put("appId", userData.getAppId());
        jwtBuilder.put("platform", userData.getPlatform());
        jwtBuilder.put("account", userData.getAccount());
        jwtBuilder.put("platformUserId", userData.getPlatformUserId());
        String token = jwtBuilder.compact();
        InnerServerInfoBean gateway = innerService.idleGateway();
        if (gateway != null) {
            gateway.setOnlineSize(gateway.getOnlineSize() + 1);
        }
        return RunResult.ok()
                .fluentPut("userId", userData.getPlatformUserId())
                .fluentPut("token", token)
                .fluentPut("host", gateway == null ? "" : gateway.getHost())
                .fluentPut("port", gateway == null ? 0 : gateway.getPort());
    }

}
