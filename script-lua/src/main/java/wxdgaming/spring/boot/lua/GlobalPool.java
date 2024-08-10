package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.core.lang.Record2;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Getter
public final class GlobalPool {

    private final String name;
    private final Globals globals;

    GlobalPool(String name) {
        this.name = name;
        this.globals = JsePlatform.standardGlobals();
        LuaJC.install(this.globals);
        set("logbackUtil", log);
        set("lua_data", LuaBus.getLua_data());
    }

    /**
     * 从 lua 字符加载
     *
     * @param luaString
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:11
     */
    public LuaValue loadString(String luaString) {
        return globals.load(luaString);
    }

    /**
     * 从 lua 字符加载
     *
     * @param luaString 脚本字符
     * @param chunkname 别名，报错的时候标记，
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:11
     */
    public LuaValue loadString(String luaString, String chunkname) {
        return globals.load(luaString, chunkname);
    }

    /**
     * 从字节流加载lua脚本
     *
     * @param lua_file    脚本名字
     * @param inputStream 字节流
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-10 11:57
     */
    public LuaValue loadInputStream(String lua_file, InputStream inputStream) {
        if (log.isDebugEnabled()) {
            log.debug("find lua script {}", lua_file);
        }
        String string = FileReadUtil.readString(inputStream);
        /* chunkname 是别名 用于标记那一段代码出错*/
        return loadString(string, lua_file).call();
    }

    /**
     * 从文件加载lua
     *
     * @param lua_file lua文件路径
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:11
     */
    public LuaValue loadFile(String lua_file) {
        if (log.isDebugEnabled()) {
            log.debug("find lua script {}", lua_file);
        }
        String string = FileReadUtil.readString(lua_file);
        /* chunkname 是别名 用于标记那一段代码出错*/
        return loadString(string, lua_file).call();
    }

    /**
     * 通过文件夹加载 .lua .LUA
     *
     * @param dir 文件夹
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 15:52
     */
    public GlobalPool loadDirs(String dir, int maxDepth) {
        FileUtil.walkFiles(dir, maxDepth, ".lua", ".LUA")
                .forEach(lua_file -> loadFile(lua_file.getPath()));
        return this;
    }

    /**
     * 从 jar 包资源文件加载
     *
     * @param classLoader  指定 classloader
     * @param package_name 指定加载的目录
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-06-27 16:12
     */
    public GlobalPool loadResources(ClassLoader classLoader, String package_name) {
        try {
            Stream<Record2<String, InputStream>> record2Stream = FileUtil.resourceStreams(classLoader, package_name);
            record2Stream.forEach(item -> {
                if (item.t1().endsWith(".lua") || item.t1().endsWith(".LUA")) {
                    if (log.isDebugEnabled()) {
                        log.debug("find lua script {}", item.t1());
                    }
                    String lua_script = FileReadUtil.readString(item.t2());
                    loadString(lua_script).call();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("package_name = " + package_name, e);
        }
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
    public GlobalPool set(String key, int value) {
        globals.set(key, value);
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
    public GlobalPool set(String key, String value) {
        globals.set(key, value);
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
    public GlobalPool set(String key, Object value) {
        globals.set(key, CoerceJavaToLua.coerce(value));
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
        return globals.get(lua_key);/*方法名称*/
    }

    public LuaValue exec(String method_name) {
        return globals.get(method_name);/*方法名称*/
    }

    public LuaValue exec(String method_name, String val1) {
        return globals.get(method_name).call(LuaValue.valueOf(val1));/*方法名称*/
    }

    public LuaValue execUserdata(String method_name, Object val1) {
        return globals.get(method_name).call(CoerceJavaToLua.coerce(val1));/*方法名称*/
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

    public CompletableFuture<LuaValue> execUserdataAsync(String method_name, Object val1) {
        return CompletableFuture.supplyAsync(() -> execUserdata(method_name, val1))
                .exceptionally(ex -> {
                    log.error("", ex);
                    return null;
                });
    }
}
