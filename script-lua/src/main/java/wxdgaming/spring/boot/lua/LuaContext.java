package wxdgaming.spring.boot.lua;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.JuaAPI;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.lang.Record2;

import java.io.Closeable;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 当前lua上下文
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-23 17:30
 **/
@Slf4j
@Getter
public class LuaContext implements Closeable {

    private final Lua L;
    private final Path[] paths;
    private volatile boolean closed = false;
    private final Map<String, LuaValue> funcCache = Maps.newHashMap();

    public LuaContext(ConcurrentHashMap<String, Object> globals, Path... paths) {
        if (1 != 1) {
            this.L = new Lua54_Sub();
        } else {
            this.L = new LuaJit_Sub();
        }
        this.L.openLibraries();
        this.paths = paths;
        for (Map.Entry<String, Object> entry : globals.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
        if (paths != null) {
            for (Path path : paths) {
                loadDir(path);
            }
        }
    }

    /** 加载一个lua文件 */
    public void loadDir(String dir) {
        loadDir(Paths.get(dir));
    }

    public void loadDir(Path dir) {
        ArrayList<String> modules = new ArrayList<>();
        ArrayList<Record2<String, byte[]>> errorPaths = new ArrayList<>();
        try {
            FileReadUtil.readBytesStream(dir.toString(), ".lua", ".LUA")
                    .sorted(Comparator.comparing(o -> o.t1().toLowerCase()))
                    .forEach(item -> {
                        try {
                            String module = load(item.t1(), item.t2());
                            modules.add(module);
                        } catch (Exception e) {
                            errorPaths.add(item);
                        }
                    });
            if (!Files.exists(dir)) return;
            // Files.walk(dir, 99)
            //         .filter(p -> {
            //             String string = p.toString();
            //             return string.endsWith(".lua") || string.endsWith(".LUA");
            //         })
            //         .filter(Files::isRegularFile)
            //         .sorted(Comparator.comparing(o -> o.toString().toLowerCase()))
            //         .forEach(filePath -> {
            //             try {
            //                 String module = loadfile(filePath);
            //                 modules.add(module);
            //             } catch (Exception e) {
            //                 errorPaths.add(filePath);
            //             }
            //         });
            if (!errorPaths.isEmpty()) {
                for (Record2<String, byte[]> errorPath : errorPaths) {
                    log.warn("lua file load error: {}", errorPath);
                    String module = load(errorPath.t1(), errorPath.t2());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("{} {}", dir, modules);
    }

    /** 加载一个lua文件 */
    public String loadfile(String filePath) {
        return loadfile(Paths.get(filePath));
    }

    /** 加载一个lua文件 */
    public String loadfile(Path filePath) {
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            return load(filePath.getFileName().toString(), bytes);
        } catch (Exception e) {
            throw new RuntimeException(filePath.toString(), e);
        }
    }

    public String load(String filePath, String script) {
        return load(filePath, script.getBytes(StandardCharsets.UTF_8));
    }

    public String load(String filePath, byte[] bytes) {
        String[] split = filePath.split("[\\\\/]");
        filePath = split[split.length - 1];
        log.debug("load lua {}", filePath);
        Buffer flip = JuaAPI.allocateDirect(bytes.length).put(bytes).flip();
        L.run(flip, filePath);
        return filePath;
    }

    /** 设置全局变量 */
    public void set(String key, LuaFunction value) {
        L.set(key, value);
    }

    /** 设置全局变量 */
    public void set(String key, Object value) {
        L.set(key, value);
    }

    public boolean has(String key) {
        LuaValue luaValue = find(key);
        return has(luaValue);
    }

    public boolean has(LuaValue luaValue) {
        return luaValue != null && luaValue.type() != Lua.LuaType.NIL;
    }

    public LuaValue find(String key) {
        return funcCache.computeIfAbsent(key, L::get);
    }

    public Object pCall(String name, Object... args) {
        LuaValue luaValue = find(name);
        if (!has(luaValue)) {
            return null;
        }
        return pCall(luaValue, args);
    }

    public Object pCall(LuaValue luaValue, Object... args) {
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
            throw Throw.of(e);
        } finally {
            L.setTop(oldTop);
        }
    }

    public void gc() {
        synchronized (this) {
            if (closed) return;
            try {
                pCall("cleanup", this.L.toString());
            } catch (Exception ignore) {
                System.out.println(this.toString() + " - cleanup error " + ignore.toString());
                L.gc();
                L.gc();
            }
        }
    }

    @Override public void close() {
        synchronized (this) {
            if (closed) return;
            closed = true;
            gc();
            funcCache.clear();
            L.close();
        }
    }

    @Override public String toString() {
        return L.toString();
    }
}
