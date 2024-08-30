package wxdgaming.spring.boot.lua;

import party.iroiro.luajava.AbstractLua;
import party.iroiro.luajava.LuaException;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.value.LuaValue;

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

    public void pCall(int nArgs, int nResults, int errfunc) throws LuaException {
        checkStack(Math.max(nResults - nArgs - 1, 0));
        checkError(C.lua_pcall(L, nArgs, nResults, errfunc), false);
    }

    @Override public void pCall(int nArgs, int nResults) throws LuaException {
        checkStack(Math.max(nResults - nArgs - 1, 0));
        checkError(C.lua_pcall(L, nArgs, nResults, 0), false);
    }

    @Override protected void checkError(int code, boolean runtime) throws LuaException {
        LuaException.LuaError error = runtime
                ? (code == 0 ? LuaException.LuaError.OK : LuaException.LuaError.RUNTIME)
                : convertError(code);
        if (error == LuaException.LuaError.OK) {
            return;
        }
        String message = "";
        if (type(-1) == LuaType.STRING) {
            message = toString(-1);
            // LuaValue[] call = ((RefLuaValue) get("errFunc")).call(message);
            LuaValue[] call = get("debug").get("traceback").call(message);
            if (call.length > 0) {
                message = call[0].toString();
            }
            pop(1);
        } else {
            message = "Lua-side error";
        }
        LuaException e = new LuaException(error, message);
        Throwable javaError = getJavaError();
        if (javaError != null) {
            e.initCause(javaError);
            error((Throwable) null);
        }
        throw e;
    }
}
