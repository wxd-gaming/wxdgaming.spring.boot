package wxdgaming.game.server.script.http.yunying;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.game.server.module.data.GlobalDataService;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.lang.RunResult;

/**
 * 运营接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-30 09:28
 **/
@Slf4j
@RestController
@RequestMapping(path = "/yunying")
public class YunyingScript extends HoldRunApplication {

    private final GlobalDataService globalDataService;

    public YunyingScript(GlobalDataService globalDataService) {
        this.globalDataService = globalDataService;
    }

    @RequestMapping(path = "/cdKeyList")
    public Object cdKeyList(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(path = "/mail")
    public Object mail(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping
    public Object banLogin(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping
    public Object banChat(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(path = "/queryRole")
    public Object queryRole(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(path = "/addPlayerGm")
    public Object addPlayerGm(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

    @RequestMapping(path = "/addAccountGm")
    public Object addAccountGm(HttpServletRequest httpContext) {

        return RunResult.ok();
    }

}
