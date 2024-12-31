package wxdgaming.spring.boot.lua;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * lua require 方式加载文件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-21 19:21
 **/
@Getter
public class LuaFileRequire {

    String luaPath = null;
    List<String> modules = new ArrayList<>();

    @SneakyThrows public LuaFileRequire(LuaFileCache luaFileCache) {
        List<ImmutablePair<Path, byte[]>> pathList = luaFileCache.getPathList();
        ArrayList<String> paths = new ArrayList<>();
        for (ImmutablePair<Path, byte[]> pair : pathList) {
            String string = pair.getLeft().getParent().toFile().getCanonicalPath() + File.separator + "?.lua";
            if (!paths.contains(string))
                paths.add(string);
            modules.add(pair.getLeft().getFileName().toString().replace(".lua", ""));
        }
        luaPath = String.join(";", paths);
        modules = List.copyOf(modules);
    }


}
