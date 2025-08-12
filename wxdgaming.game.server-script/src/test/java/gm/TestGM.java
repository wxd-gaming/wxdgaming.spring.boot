package gm;

import wxdgaming.game.server.script.http.gm.dynamiccode.IGmDynamic;
import wxdgaming.spring.boot.core.ApplicationContextProvider;

public class TestGM implements IGmDynamic {


    @Override public Object execute(ApplicationContextProvider runApplication) {
        return "远程3";
    }

}
