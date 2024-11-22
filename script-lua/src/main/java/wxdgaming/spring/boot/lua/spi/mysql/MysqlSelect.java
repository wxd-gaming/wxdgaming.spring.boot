package wxdgaming.spring.boot.lua.spi.mysql;

import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.lua.LuaJavaSpi;

/**
 * mysql查询
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-22 20:40
 **/
public class MysqlSelect implements LuaJavaSpi {
    @Override public String getName() {
        return "sql-select";
    }

    @Override public Object doAction(Lua L, Object[] args) {
        return null;
    }
}
