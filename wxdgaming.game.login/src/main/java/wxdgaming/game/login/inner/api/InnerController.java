package wxdgaming.game.login.inner.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.login.inner.InnerService;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.timer.MyClock;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:41
 **/
@Slf4j
@RestController
@RequestMapping(path = "/inner")
public class InnerController extends HoldRunApplication {

    final InnerService innerService;

    public InnerController(InnerService innerService) {
        this.innerService = innerService;
    }

    @RequestMapping(path = "/registerGame")
    public RunResult registerGame(HttpServletRequest request, @RequestBody JSONObject data) {
        ArrayList<Integer> sidList = data.getObject("sidList", new TypeReference<ArrayList<Integer>>() {});
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        for (Integer sid : sidList) {
            InnerServerInfoBean clone = serverBean.clone();
            clone.setServerId(sid);
            clone.setHost(SpringUtil.getClientIp(request));
            clone.setLastSyncTime(MyClock.millis());
            innerService.getInnerGameServerInfoMap().put(sid, clone);
            innerService.getSqlDataHelper().getDataBatch().save(clone);
        }
        return RunResult.ok();
    }

    @RequestMapping(path = "/registerGateway")
    public RunResult registerGateway(HttpServletRequest request, @RequestBody JSONObject data) {
        Integer sid = data.getInteger("sid");
        String jsonBean = data.getString("serverBean");
        InnerServerInfoBean serverBean = FastJsonUtil.parse(jsonBean, InnerServerInfoBean.class);
        serverBean.setHost(SpringUtil.getClientIp(request));
        serverBean.setLastSyncTime(MyClock.millis());
        innerService.getInnerGatewayServerInfoMap().put(sid, serverBean);
        return gameMainServerList();
    }

    public RunResult gameMainServerList() {
        List<InnerServerInfoBean> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .filter(v -> v.getMainId() == v.getServerId())
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

    @RequestMapping(path = "/gameServerList")
    public RunResult gameServerList(HttpServletRequest context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGameServerInfoMap().values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

    @RequestMapping(path = "/gatewayServerList")
    public RunResult gatewayServerList(HttpServletRequest context, @RequestBody JSONObject data) {
        List<InnerServerInfoBean> list = innerService.getInnerGatewayServerInfoMap().values()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getServerId(), o1.getServerId()))
                .toList();
        return RunResult.ok().data(list);
    }

}
