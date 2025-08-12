package gm;

import wxdgaming.game.server.api.TestApi;
import wxdgaming.game.server.script.http.gm.dynamiccode.IGmDynamic;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;

import java.lang.reflect.Field;

public class ExecGM implements IGmDynamic {


    @Override public Object execute(ApplicationContextProvider runApplication) throws Exception {
        TestApi instance = runApplication.getApplicationContext().getBean(TestApi.class);
        instance.strMap.put("a", "2");
        // player = instance.getPlayer();
        // player.setName("aabb");
        Field str2Map = TestApi.class.getDeclaredField("str2Map");
        Object object = str2Map.get(instance);

        return FastJsonUtil.toJSONString(instance.strMap);
    }

}
