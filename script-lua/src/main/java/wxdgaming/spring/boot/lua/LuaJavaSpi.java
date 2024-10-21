package wxdgaming.spring.boot.lua;

/**
 * 用于把java对象注入到lua中
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 20:47
 **/
public interface LuaJavaSpi extends LuaFunction {

    String getName();

}
