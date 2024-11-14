package wxdgaming.spring.boot.core.script;

import java.io.Serializable;

/**
 * 单例脚本接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-13 20:52
 **/
public interface IScriptSingleton<Key extends Serializable> extends IScript<Key> {}
