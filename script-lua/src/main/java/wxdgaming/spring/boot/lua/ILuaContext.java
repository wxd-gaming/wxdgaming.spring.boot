package wxdgaming.spring.boot.lua;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public interface ILuaContext extends Closeable, AutoCloseable {

    /**
     * 通过文件字节加载
     *
     * @param list    需要配加载的文件列表
     * @param fortune 加载权重，1为不重试，2为重试一次，3为重试两次，以此类推，默认为1
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-11-07 15:24
     */
    default boolean load(List<ImmutablePair<Path, byte[]>> list, int fortune) {
        if (fortune < 1) return false;
        List<ImmutablePair<Path, byte[]>> error = new ArrayList<>();
        for (ImmutablePair<Path, byte[]> immutablePair : list) {
            String string = immutablePair.getLeft().getFileName().toString();
            try {
                loadFile4Bytes(string, immutablePair.getRight(), fortune);
            } catch (Exception e) {
                if (fortune > 1) {
                    error.add(immutablePair);
                } else {
                    throw new RuntimeException(string, e);
                }
            }
        }
        if (!error.isEmpty()) {
            return load(error, fortune - 1);
        }
        return true;
    }

    void loadFile4Bytes(String fileName, byte[] bytes, int fortune);

    boolean has(String name);

    Object call(boolean xpcall, String key, Object... args);

    void memory(AtomicLong memory);

    void gc();

    @Override void close();

    boolean isClosed();

    String getName();
}
