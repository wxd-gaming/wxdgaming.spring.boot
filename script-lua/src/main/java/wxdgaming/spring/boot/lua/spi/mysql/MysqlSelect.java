package wxdgaming.spring.boot.lua.spi.mysql;

import org.springframework.stereotype.Service;
import party.iroiro.luajava.Lua;
import wxdgaming.spring.boot.data.batis.DbHelper;
import wxdgaming.spring.boot.lua.LuaJavaSpi;

/**
 * mysql查询
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-22 20:40
 **/
@Service
public class MysqlSelect implements LuaJavaSpi {

    final DbHelper dbHelper;

    public MysqlSelect(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override public String getName() {
        return "mysqlSelect";
    }

    @Override public Object doAction(Lua L, Object[] args) {

        return null;
    }
}
