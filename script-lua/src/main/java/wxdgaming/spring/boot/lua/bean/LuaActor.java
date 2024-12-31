package wxdgaming.spring.boot.lua.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-09 10:15
 **/
@Getter
@Setter
public class LuaActor {

    public int lv = 99;
    public Long uid;
    public String name;

    public LuaActor() {
    }

    public LuaActor(Long uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public Long getUid() {
        System.out.println("java method getUid");
        return uid;
    }

    public String getName() {
        System.out.println("java method getName");
        return name;
    }

    @Override public String toString() {
        return "LuaActor{" +
               "lv=" + lv +
               ", uid=" + uid +
               ", name='" + name + '\'' +
               '}';
    }
}
