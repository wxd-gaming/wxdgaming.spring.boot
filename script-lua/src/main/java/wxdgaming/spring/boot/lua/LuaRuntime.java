package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * lua 装载器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 16:10
 */
@Slf4j
@Getter
public class LuaRuntime implements Closeable {

    final String name;
    final Lua lua54;
    final ThreadLocal<LuaContext> threadLocal;

    public LuaRuntime(String name) {
        this.name = name;
        lua54 = new Lua54();
        lua54.openLibraries();
        threadLocal = ThreadLocal.withInitial(this::newContext);
    }

    /** 加载一个lua文件 */
    public void loadDir(String dir) {
        try {
            Files.walk(Paths.get(dir), 99)
                    .filter(p -> {
                        String string = p.toString();
                        return string.endsWith(".lua") || string.endsWith(".LUA");
                    })
                    .filter(Files::isRegularFile)
                    .forEach(this::loadfile);
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
        Buffer flip = ByteBuffer.allocateDirect(bytes.length).put(bytes).flip();
        lua54.run(flip, filePath);
    }

    /** 设置全局变量 */
    public void set(String key, JavaFunction value) {
        lua54.set(key, value);
    }

    /** 设置全局变量 */
    public void set(String key, Object value) {
        lua54.set(key, value);
    }

    public LuaContext newContext() {
        return new LuaContext(lua54.newThread());
    }

    public LuaContext context() {
        return threadLocal.get();
    }

    /** 关闭资源 */
    @Override public void close() throws IOException {
        lua54.close();
    }
}
