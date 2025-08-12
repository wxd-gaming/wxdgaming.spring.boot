package wxdgaming.game.login.service.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import wxdgaming.game.login.AppPlatformParams;
import wxdgaming.game.login.sdk.AbstractSdkLoginApi;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:41
 **/
@Slf4j
@RestController
@RequestMapping(path = "/login")
public class LoginController {

    Map<AppPlatformParams.Platform, AbstractSdkLoginApi> sdkMap = new HashMap<>();

    @Init
    public void init(ApplicationContextProvider applicationContextProvider) {

        HashMap<AppPlatformParams.Platform, AbstractSdkLoginApi> map = new HashMap<>();

        applicationContextProvider.classWithSuper(AbstractSdkLoginApi.class)
                .forEach(sdkLoginApi -> {
                    AbstractSdkLoginApi oldPut = map.put(sdkLoginApi.platform(), sdkLoginApi);
                    AssertUtil.assertTrue(oldPut == null, "重复注册类型：" + sdkLoginApi.platform());
                    log.info("register sdk login api: {}", sdkLoginApi.platform());
                });

        sdkMap = Collections.unmodifiableMap(map);
    }

    @RequestMapping(path = "check")
    public RunResult check(HttpServletRequest request, @RequestParam("appId") int appId) {
        AppPlatformParams appPlatformParams = AppPlatformParams.getAppPlatformParams(appId);
        if (appPlatformParams == null) {
            return RunResult.fail("not support appId: " + appId + " not exist");
        }
        AppPlatformParams.Platform platform = appPlatformParams.getPlatform();
        AbstractSdkLoginApi sdkLoginApi = sdkMap.get(platform);
        if (sdkLoginApi == null) {
            return RunResult.fail("not support platform: " + platform);
        }
        return sdkLoginApi.login(request, appPlatformParams);
    }

    @RequestMapping(path = "test/{id}/sdk")
    public RunResult checkSdk(HttpServletRequest request, @PathVariable("id") int id) {
        return RunResult.fail(String.valueOf(id));
    }

    @RequestMapping(path = "test/{id}/v1")
    public RunResult testV1(HttpServletRequest request, @RequestBody String body) {
        log.info("body: {}", body);
        return RunResult.ok().fluentPut("data", body);
    }

}
