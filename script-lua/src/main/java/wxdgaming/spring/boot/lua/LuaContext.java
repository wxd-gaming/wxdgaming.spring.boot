package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import party.iroiro.luajava.Consts;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.value.LuaValue;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.core.lang.Record2;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    public LuaContext(ConcurrentHashMap<String, Object> globals, Path... paths) {
        this.L = new Lua54_Sub();
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
    public void loadDir(Path dir) {
        List<Record2<String, InputStream>> errors = new ArrayList<>();
        try {
            FileUtil
                    .resourceStreams(this.getClass().getClassLoader(), dir.toString())
                    .filter(item -> item.t1().endsWith(".lua") || item.t1().endsWith(".LUA"))
                    .sorted(Comparator.comparing(o -> o.t1().toLowerCase()))
                    .forEach(item -> {
                        try {
                            String string = FileReadUtil.readString(item.t2());
                            this.load(item.t1(), string);
                        } catch (Exception e) {
                            log.error("load lua error {} {}", item.t1(), e.toString());
                            errors.add(item);
                        }
                    });
            for (Record2<String, InputStream> file : errors) {
                String string = FileReadUtil.readString(file.t2());
                this.load(file.t1(), string);
            }
            log.info("{}", dir);
        } catch (Exception e) {
            throw new RuntimeException("dir: " + dir, e);
        }
    }

    /** 加载一个lua文件 */
    public void loadfile(String filePath) {
        loadfile(Paths.get(filePath));
    }

    /** 加载一个lua文件 */
    public void loadfile(Path filePath) {
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            load(filePath.toString(), bytes);
        } catch (Exception e) {
            throw new RuntimeException(filePath.toString(), e);
        }
    }

    public void load(String filePath, String script) {
        load(filePath, script.getBytes(StandardCharsets.UTF_8));
    }

    public void load(String filePath, byte[] bytes) {
        filePath = filePath.replace("\\", "/");
        String name = FilenameUtils.getName(filePath);
        log.debug("load lua name={}, path={}", name, filePath);
        Buffer flip = ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();
        L.run(flip, name);
    }

    public boolean has(String key) {
        LuaValue luaValue = L.get(key);
        return has(luaValue);
    }

    public boolean has(LuaValue luaValue) {
        return luaValue != null && luaValue.type() != Lua.LuaType.NIL;
    }

    public LuaValue find(String key) {
        return L.get(key);
    }

    /** 设置全局变量，全局函数会有线程共享问题 */
    public void set(String key, JavaFunction value) {
        L.set(key, value);
    }

    /** 设置全局变量 */
    public void set(String key, Object value) {
        L.set(key, value);
    }

    public LuaValue pCall(String key, Object... args) {
        LuaValue luaValue = find(key);
        return pCall(luaValue, args);
    }

    public LuaValue pCall(LuaValue luaValue, Object... args) {
        int top = L.getTop();
        luaValue.push(L);
        for (Object o : args) {
            L.push(o, Lua.Conversion.FULL);
        }
        L.pCall(args.length, Consts.LUA_MULTRET);
        int returnCount = L.getTop() - top;
        if (returnCount == 0) {
            return null;
        }
        LuaValue[] call = new LuaValue[returnCount];
        for (int i = 0; i < returnCount; i++) {
            call[returnCount - i - 1] = L.get();
        }
        LuaValue value = call[0];
        if (value.type() == Lua.LuaType.NONE || value.type() == Lua.LuaType.NIL) {
            return null;
        }
        return value;
    }

    @Override public void close() {
        if (closed) return;
        closed = true;
        L.close();
    }

}
