package wxdgaming.game.server.script.http.gm.dynamiccode;


import wxdgaming.spring.boot.core.ApplicationContextProvider;

/**
 * gm动态代码
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-03-29 18:54
 **/
public interface IGmDynamic {

    Object execute(ApplicationContextProvider runApplication) throws Exception;

}
