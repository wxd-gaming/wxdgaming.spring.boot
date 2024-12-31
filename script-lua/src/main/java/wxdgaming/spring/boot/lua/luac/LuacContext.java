package wxdgaming.spring.boot.lua.luac;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.JuaAPI;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.spring.boot.core.GlobalUtil;
import wxdgaming.spring.boot.lua.ILuaContext;
import wxdgaming.spring.boot.lua.LuaRuntime;
import wxdgaming.spring.boot.lua.luac.impl.Lua54Impl;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * lua 当前上下文
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 16:57
 */
@Slf4j
@Getter
public class LuacContext implements ILuaContext {

    boolean closed = false;
    final String name;
    final Lua L;
    HashMap<String, LuaValue> funcCache = new HashMap<>();

    public LuacContext(LuaRuntime luacRuntime) {
        L = new Lua54Impl();
        this.name = luacRuntime.getName() + " - " + Thread.currentThread().getName();
        L.openLibraries();

        {
            // 设置 Lua 文件的搜索路径
            L.getGlobal("package");
            L.getField(-1, "path");
            String currentPath = L.toString(-1);
            String newPath = luacRuntime.getLuaFileRequire().getLuaPath() + ";" + currentPath;
            L.pop(1); // 移除当前路径
            L.push(newPath);
            L.setField(-2, "path");
            L.set("paths", luacRuntime.getLuaFileRequire().getLuaPath());
        }
        L.set("ldebug", GlobalUtil.DEBUG.get());
        // 加载内置资源
        load(luacRuntime.getLuaFileCache().getExtendList(), 5);

        for (Map.Entry<String, Object> entry : luacRuntime.getGlobals().entrySet()) {
            L.set(entry.getKey(), entry.getValue());
            log.debug("global lua {}", entry.getKey());
        }

        List<String> modules = luacRuntime.getLuaFileRequire().getModules();
        requireLoad(modules, 5);
    }

    /**
     * require 加载文件形式会缓存，只加载一次，dofile 调用一次加载一次，不会缓存，
     *
     * @param modules 需要加载模块
     * @param fortune 加载权重，1为不重试，2为重试一次，3为重试两次，以此类推，默认为1
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-11-07 15:22
     */
    void requireLoad(List<String> modules, int fortune) {
        if (fortune < 1) return;
        List<String> error = new ArrayList<>();
        for (String module : modules) {
            try {
                L.run("require('" + module + "')");
                log.debug("require load lua {}", module);
            } catch (Exception e) {
                if (fortune > 1) {
                    error.add(module);
                } else {
                    throw new RuntimeException(module, e);
                }
            }
        }
        if (!error.isEmpty()) {
            requireLoad(error, fortune - 1);
        }
    }

    @Override public void loadFile4Bytes(String fileName, byte[] bytes, int fortune) {
        Buffer flip = JuaAPI.allocateDirect(bytes.length).put(bytes).flip();
        L.run(flip, fileName);
        log.debug("file byte load lua {}", fileName);
    }

    @Override public boolean has(String name) {
        return findLuaValue(name) != null;
    }

    public LuaValue findLuaValue(String name) {
        return funcCache.computeIfAbsent(name, f -> {
            LuaValue value = L.get(name);
            return value.type() == Lua.LuaType.NIL || value.type() == Lua.LuaType.NONE ? null : value;
        });
    }

    @Override public Object call(boolean xpcall, String key, Object... args) {
        synchronized (this) {
            return call0(xpcall, key, args);
        }
    }

    Object call0(boolean xpcall, String key, Object... args) {
        LuaValue luaValue = findLuaValue(key);
        if (luaValue == null) {
            return null;
        }
        if (xpcall) {
            LuaValue dispatchLua = findLuaValue("dispatch");
            if (dispatchLua == null) throw new RuntimeException("lua function dispatch not found");
            Object[] args2 = new Object[args.length + 1];
            System.arraycopy(args, 0, args2, 1, args.length);
            args2[0] = key;
            return pcall0(dispatchLua, args2);
        } else {
            return pcall0(luaValue, args);
        }
    }

    Object pcall0(LuaValue luaValue, Object... args) {
        int oldTop = L.getTop();
        luaValue.push(L);
        for (Object o : args) {
            LuaUtils.push(L, o);
        }
        try {
            L.pCall(args.length, Consts.LUA_MULTRET);
            int returnCount = L.getTop() - oldTop;
            if (returnCount == 0) {
                return null;
            }
            LuaValue[] call = new LuaValue[returnCount];
            for (int i = 0; i < returnCount; i++) {
                call[returnCount - i - 1] = L.get();
            }
            LuaValue returnValue = call[0];
            return LuaUtils.luaValue2Object(returnValue);
        } catch (Throwable e) {
            RuntimeException runtimeException = new RuntimeException(e.getMessage());
            runtimeException.setStackTrace(e.getStackTrace());
            throw runtimeException;
        } finally {
            L.setTop(oldTop);
        }
    }

    @Override public void memory(AtomicLong memory) {
        synchronized (this) {
            LuaValue luaValue = findLuaValue("memory0");
            Object pcall = pcall0(luaValue);
            memory.addAndGet(((Number) pcall).longValue());
        }
    }

    @Override public void gc() {
        synchronized (this) {
            if (closed) return;
            try {
                this.L.gc();
            } catch (Exception e) {
                log.error("{} - cleanup error {}", this.toString(), e.toString());
            }
        }
    }

    @Override public void close() {
        synchronized (this) {
            if (closed) return;
            closed = true;
            gc();
            funcCache = new HashMap<>();
            L.close();
        }
    }

}
