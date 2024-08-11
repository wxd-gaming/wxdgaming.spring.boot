package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import wxdgaming.spring.boot.core.function.Predicate2;
import wxdgaming.spring.boot.core.io.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * lua 脚本 加载器 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-06-27 10:10
 **/
@Slf4j
@Getter
public class LuaBus {

    @Getter private static final ConcurrentHashMap<String, Object> lua_data = new ConcurrentHashMap<>();

    /**
     * @param classLoader
     * @param path        有限查找物理文件，如果路径不存在，在查找 resources 文件
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-10 11:45
     */
    public static LuaBus buildFromStreams(ClassLoader classLoader, String path) {
        LuaBus luaBus = new LuaBus();
        GlobalPool globalPool = new GlobalPool("resource");
        FileUtil.resourceStreams(classLoader, path)
                .filter(v -> v.t1().endsWith(".lua") || v.t1().endsWith(".LUA"))
                .forEach(v -> {
                    globalPool.loadInputStream(v.t1(), v.t2());
                });
        luaBus.getGlobalPools().put(globalPool.getName(), globalPool);
        return luaBus;
    }

    /**
     * 规则目录下面
     * --script
     * --------模块1
     * --------模块2
     * --------模块3
     * --util 公共脚本
     *
     * @param base_dir 主目录
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-28 17:33
     */
    public static LuaBus buildFromDirs(String base_dir) {
        LuaBus luaBus = new LuaBus();

        File script_path = new File(base_dir + "/script");

        FileUtil.walkDirs(script_path.getPath(), 1).forEach(dir -> {
            if (dir.equals(script_path)) return;
            log.info("load lua module：{} - {}", dir, dir.getName());
            GlobalPool globalPool = new GlobalPool(dir.getName());
            globalPool.loadDirs(dir.getPath(), 99);
            globalPool.loadDirs(base_dir + "/util", 99);
            luaBus.globalPools.put(globalPool.getName(), globalPool);
        });

        return luaBus;
    }

    private final HashMap<String, GlobalPool> globalPools = new HashMap<>();

    public GlobalPool globalPool(String name) {
        return globalPools.get(name);
    }

    public GlobalPool globalPoolNew(String name) {
        return globalPools.computeIfAbsent(name, l -> new GlobalPool(name));
    }

    public void forExec(String method) {
        forExec(method, null);
    }

    public void forExec(String method, BiConsumer<String, LuaValue> consumer) {
        for (Map.Entry<String, GlobalPool> poolEntry : globalPools.entrySet()) {
            LuaValue luaValue = poolEntry.getValue().get(method);
            if (luaValue != null) {
                LuaValue callLuaValue = luaValue.call();
                if (consumer != null) {
                    consumer.accept(poolEntry.getKey(), callLuaValue);
                }
            }
        }
    }

    public void forExecTry(String method) {
        forExecTry(method, null);
    }

    public void forExecTry(String method, BiConsumer<String, LuaValue> consumer) {
        predicateTry(method, (name, luaValue) -> {
            LuaValue ret = luaValue.call();
            if (consumer != null) {
                consumer.accept(name, ret);
            }
            return false;
        });
    }

    public void forExecTry(String method, String data, BiConsumer<String, LuaValue> consumer) {
        runTry(method, LuaValue.valueOf(data), consumer);
    }

    public void forExecTry(String method, Object data, BiConsumer<String, LuaValue> consumer) {
        runTry(method, CoerceJavaToLua.coerce(data), consumer);
    }

    /**
     * @param method   方法名字
     * @param data     数据
     * @param consumer
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-07-29 10:51
     */
    public void runTry(String method, LuaValue data, BiConsumer<String, LuaValue> consumer) {
        predicateTry(method, (name, luaValue) -> {
            LuaValue ret = luaValue.call(data);
            if (consumer != null) {
                consumer.accept(name, ret);
            }
            return false;
        });
    }


    public void predicateTry(String method, Predicate2<String, LuaValue> exec) {
        for (Map.Entry<String, GlobalPool> poolEntry : globalPools.entrySet()) {
            LuaValue luaValue = poolEntry.getValue().get(method);
            if (luaValue != null && !luaValue.isnil()) {
                try {
                    if (exec.test(poolEntry.getKey(), luaValue)) {
                        return;
                    }
                } catch (Exception e) {
                    log.error("name={}, method={}", poolEntry.getKey(), method, e);
                }
            }
        }
    }

    /**
     * 设置全局变量
     *
     * @param key   存储变量名
     * @param value 变量的值
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:13
     */
    public LuaBus set(String key, int value) {
        globalPools.values().forEach(g -> g.set(key, value));
        return this;
    }

    /**
     * 设置全局变量
     *
     * @param key   存储变量名
     * @param value 变量的值
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:13
     */
    public LuaBus set(String key, String value) {
        globalPools.values().forEach(g -> g.set(key, value));
        return this;
    }

    /**
     * 设置全局变量
     *
     * @param key   存储变量名
     * @param value 变量的值
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:13
     */
    public LuaBus set(String key, Object value) {
        globalPools.values().forEach(g -> g.set(key, CoerceJavaToLua.coerce(value)));
        return this;
    }

    /**
     * 设置全局变量
     *
     * @param key              存储变量名
     * @param valueArgFunction 变量的值
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:13
     */
    public LuaBus set(String key, VarArgFunction valueArgFunction) {
        globalPools.values().forEach(g -> g.set(key, valueArgFunction));
        return this;
    }

    /**
     * 查找 lua 虚拟机的对象
     *
     * @param lua_key 查找的 key值 或者 方法名称
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 10:17
     */
    public LuaValue get(String lua_key) {
        for (GlobalPool value : globalPools.values()) {
            LuaValue luaValue = value.get(lua_key);
            if (luaValue != null) {
                return luaValue;
            }
        }
        return null;
    }

    public LuaValue exec(String method_name) {
        LuaValue luaValue = get(method_name);
        if (luaValue != null) {
            return luaValue.call();
        }
        throw new NullPointerException("method_name=" + method_name);
    }

    public LuaValue exec(String method_name, String val1) {
        LuaValue luaValue = get(method_name);
        if (luaValue != null) {
            return luaValue.call(val1);
        }
        throw new NullPointerException("method_name=" + method_name);
    }

    public LuaValue exec(String method_name, Object... params) {
        LuaValue luaValue = get(method_name);
        if (luaValue != null) {
            LuaValue[] luaValues = convert(params);
            Varargs invoke = luaValue.invoke(luaValues);
            if (invoke != null && invoke != LuaValue.NIL && invoke.narg() > 0) {
                return invoke.arg1();
            }
            return null;
        }
        throw new NullPointerException("method_name=" + method_name);
    }

    /**
     * @param method   需要调用的方法
     * @param consumer 执行回调
     * @param params   具体参数
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-07-29 10:55
     */
    public void forExec(String method, BiConsumer<String, LuaValue> consumer, Object... params) {
        LuaValue[] luaValues = convert(params);
        for (Map.Entry<String, GlobalPool> poolEntry : globalPools.entrySet()) {
            LuaValue luaValue = poolEntry.getValue().get(method);
            if (luaValue != null) {
                Varargs invoke = luaValue.invoke(luaValues);
                LuaValue ret = null;
                if (invoke != null && invoke != LuaValue.NIL && invoke.narg() > 0) {
                    ret = invoke.arg1();
                }
                consumer.accept(poolEntry.getKey(), ret);
            }
        }
    }

    public LuaValue execUserdata(String method_name, Object val1) {
        LuaValue luaValue = get(method_name);
        if (luaValue != null) {
            return luaValue.call(CoerceJavaToLua.coerce(val1));
        }
        throw new NullPointerException("method_name=" + method_name);
    }

    public CompletableFuture<LuaValue> execAsync(String method_name) {
        return CompletableFuture.supplyAsync(() -> exec(method_name))
                .exceptionally(ex -> {
                    log.error("", ex);
                    return null;
                });
    }

    public CompletableFuture<LuaValue> execAsync(String method_name, String val1) {
        return CompletableFuture.supplyAsync(() -> exec(method_name, val1))
                .exceptionally(ex -> {
                    log.error("", ex);
                    return null;
                });
    }

    public CompletableFuture<Void> execUserdataAsync(String method_name, Object val1) {
        return CompletableFuture.runAsync(() -> {
                    forExecTry(method_name, val1, null);
                })
                .exceptionally(ex -> {
                    log.error("", ex);
                    return null;
                });
    }

    public LuaValue[] convert(Object... params) {
        LuaValue[] luaValues = new LuaValue[params.length];
        for (int i = 0; i < params.length; i++) {
            luaValues[i] = CoerceJavaToLua.coerce(params[i]);
        }
        return luaValues;
    }

}
