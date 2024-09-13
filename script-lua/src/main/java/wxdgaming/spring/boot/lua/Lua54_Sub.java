package wxdgaming.spring.boot.lua;

import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.lua54.Lua54;

/**
 * 子类，重写 Lua54 pushArray to pushJavaArray
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-28 20:29
 */
public class Lua54_Sub extends Lua54 {

    public Lua54_Sub() throws LinkageError {
    }

    public Lua54_Sub(long L, int id, AbstractLua main) {
        super(L, id, main);
    }

    @Override public Lua54_Sub newThread() {
        return (Lua54_Sub) super.newThread();
    }

    @Override protected Lua54_Sub newThread(long L, int id, AbstractLua mainThread) {
        return new Lua54_Sub(L, id, mainThread);
    }

    @Override public void checkStack(int extra) throws RuntimeException {
        super.checkStack(extra);
    }

    @Override public void pushArray(Object array) throws IllegalArgumentException {
        pushJavaArray(array);
    }

    @Override public void pCall(int nArgs, int nResults) throws LuaException {
        super.pCall(nArgs, nResults);
    }
}
