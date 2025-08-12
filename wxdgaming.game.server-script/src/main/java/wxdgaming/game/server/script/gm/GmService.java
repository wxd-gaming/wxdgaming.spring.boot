package wxdgaming.game.server.script.gm;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.gm.ann.GM;
import wxdgaming.game.server.script.tips.TipsService;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

/**
 * gm服务, 运营接口
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 09:23
 **/
@Slf4j
@Service
public class GmService extends HoldRunApplication {

    HashMap<String, ApplicationContextProvider.MethodContent> gmMap = new HashMap<>();
    private final TipsService tipsService;

    public GmService(TipsService tipsService) {
        this.tipsService = tipsService;
    }

    @Init
    public void init() {
        HashMap<String, ApplicationContextProvider.MethodContent> tmp = new HashMap<>();
        runApplication.withMethodAnnotated(GM.class)
                .forEach(content -> {
                    Method method = content.getMethod();
                    ApplicationContextProvider.MethodContent old = tmp.put(method.getName().toLowerCase(), content);
                    AssertUtil.assertTrue(old == null, "重复的gm命令: " + method.getName());
                });
        gmMap = tmp;
    }

    public void doGm(Player player, String[] args) {
        JSONArray jsonArray = new JSONArray(List.of(args));
        String cmd = jsonArray.getString(0).toLowerCase();
        ApplicationContextProvider.MethodContent providerMethod = gmMap.get(cmd);
        if (providerMethod == null) {
            tipsService.tips(player, "不存在的gm命令: " + cmd);
            return;
        }
        Method method = providerMethod.getMethod();
        try {
            method.invoke(providerMethod.getBean(), player, jsonArray);
        } catch (Exception e) {
            log.error("执行gm命令失败: {}", cmd, e);
            tipsService.tips(player, "执行gm命令失败: " + cmd);
        }
    }

}
