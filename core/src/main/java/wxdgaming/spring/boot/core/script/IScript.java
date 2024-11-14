package wxdgaming.spring.boot.core.script;

import java.io.Serializable;

/**
 * 脚本接口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-13 20:52
 **/

public interface IScript<Key extends Serializable> {

    Key scriptKey();

}