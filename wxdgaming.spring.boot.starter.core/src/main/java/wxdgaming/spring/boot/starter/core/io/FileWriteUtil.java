package wxdgaming.spring.boot.starter.core.io;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.Throw;
import wxdgaming.spring.boot.starter.core.function.ConsumerE1;

import java.io.File;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-08-18 15:47
 **/
@Slf4j
public class FileWriteUtil implements Serializable {

    /**
     * 覆盖 写入文件
     *
     * @param fileName 文件
     * @param content  内容
     */
    public static void writeString(String fileName, String content) {
        writeString(fileName, content, false);
    }

    public static void writeString(String fileName, String content, boolean append) {
        File file = new File(fileName);
        writeString(file, content, append);
    }

    /**
     * 覆盖 写入文件
     *
     * @param file    文件
     * @param content 内容
     */
    public static void writeString(File file, String content) {
        writeString(file, content, false);
    }

    public static void writeString(File file, String content, boolean append) {
        writeBytes(file, content.getBytes(StandardCharsets.UTF_8), append);
    }

    /**
     * 覆盖 写入文件
     *
     * @param fileName 文件
     * @param bytes    内容
     */
    public static void writeBytes(String fileName, byte[] bytes) {
        final File file = new File(fileName);
        writeBytes(file, bytes);
    }

    /**
     * 覆盖 写入文件
     *
     * @param file  文件
     * @param bytes 内容
     */
    public static void writeBytes(File file, byte[] bytes) {
        writeBytes(file, bytes, false);
    }

    /**
     * 写入文件
     *
     * @param file   文件
     * @param bytes  内容
     * @param append 是否追加
     */
    public static void writeBytes(File file, byte[] bytes, boolean append) {
        fileOutputStream(
                file,
                append,
                (outputStream) -> {
                    int len = bytes.length;
                    int rem = len;
                    while (rem > 0) {
                        /*默认8k对齐缓冲*/
                        int n = Math.min(rem, 8192);
                        outputStream.write(bytes, (len - rem), n);
                        rem -= n;
                    }
                }
        );
    }

    public static void fileOutputStream(File file, boolean append, ConsumerE1<OutputStream> call) {
        FileUtil.mkdirs(file);
        OpenOption[] openOptions;
        if (append) {
            openOptions = new OpenOption[2];
            /*追加文本,如果文件不存在则创建*/
            openOptions[0] = StandardOpenOption.CREATE;
            openOptions[1] = StandardOpenOption.APPEND;
        } else {
            openOptions = new OpenOption[2];
            /*非追加文本，如果就要重新创建文件*/
            openOptions[0] = StandardOpenOption.CREATE;
            /*将文件字节流截断为0，这样就达到覆盖文件目的*/
            openOptions[1] = StandardOpenOption.TRUNCATE_EXISTING;
        }
        try (OutputStream out = Files.newOutputStream(file.toPath(), openOptions)) {
            call.accept(out);
        } catch (Throwable e) {
            throw Throw.of(e);
        }
    }

    /**
     * 拷贝当前文件
     *
     * @param file
     * @param outPath
     */
    public static void copy(String file, String outPath) {
        Path file1 = FileUtil.findPath(file);
        copy(file1, outPath);
    }

    /**
     * 拷贝当前文件
     *
     * @param file
     * @param outPath
     */
    public static void copy(Path file, String outPath) {
        byte[] bytes = FileReadUtil.readBytes(file);
        writeBytes(outPath + "/" + file.getFileName(), bytes);
    }

    /**
     * 拷贝当前文件夹，不包含子文件夹
     *
     * @param dir
     * @param outPath
     */
    public static void copyDir(String dir, String outPath) {
        FileUtil.walkFiles(dir, 1).forEach(f -> {
            copy(f, outPath);
        });
    }
}
