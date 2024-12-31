package wxdgaming.spring.boot.lua.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * lua参数
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-30 11:30
 **/
@Getter
@Setter
public class LuaMap {
    public long uid;
    public int cfgId;

    public LuaMap(int cfgId, long uid) {
        this.cfgId = cfgId;
        this.uid = uid;
    }

    @Override public String toString() {
        return "LuaMap{" +
               "cfgId=" + cfgId +
               ", uid=" + uid +
               '}';
    }
}
