package wxdgaming.spring.boot.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.lang.Record2;
import wxdgaming.spring.boot.core.zip.ZipReadFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * 文件辅助
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-08-18 14:40
 **/
public class FileUtil implements Serializable {

    private static Logger log() {
        return LoggerFactory.getLogger(FileUtil.class);
    }

    private static final String[] empty = new String[0];

    /** 返回绝对路径 */
    public static String getCanonicalPath(String fileName) {
        return getCanonicalPath(new File(fileName));
    }

    /** 返回绝对路径 */
    public static String getCanonicalPath(File fileName) {
        try {
            return fileName.getCanonicalPath();
        } catch (Exception e) {
            throw Throw.of(e);
        }
    }

    /** 根据传入的类，获取类的jar包路径 */
    public static String clazzJarPath(Class clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    /**
     * 优先读取 config 目录下面
     * <p> 如果没有，从resources 文件夹下面读取
     */
    public static Path findPath(String fileName) {
        return findPath(fileName, Thread.currentThread().getContextClassLoader());
    }

    public static Path findPath(String fileName, ClassLoader classLoader) {

        fileName = fileName.replace("\\", "/");

        Path filePath = Paths.get(fileName);
        if (exists(filePath)) {
            return filePath;
        }

        if (!fileName.startsWith("config")) {
            filePath = Paths.get("config/" + fileName);
            if (exists(filePath)) {
                return filePath;
            }
        }

        {
            URL resource = classLoader.getResource(fileName);
            if (resource != null) {
                String decode = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
                return Paths.get(decode);
            }
        }
        return null;
    }

    /** 获取资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Record2<String, InputStream> findInputStream(String fileName) {
        return findInputStream(Thread.currentThread().getContextClassLoader(), fileName);
    }

    /** 获取资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Record2<String, InputStream> findInputStream(ClassLoader classLoader, String fileName) {
        return resourceStreams(classLoader, fileName).findFirst().orElse(null);
    }

    /** 获取所有资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Stream<Record2<String, InputStream>> resourceStreams(final String path, String... extendNames) {
        return resourceStreams(Thread.currentThread().getContextClassLoader(), path, extendNames);
    }

    /** 获取所有资源 <br>如果传入的目录本地文件夹没有，<br>会查找本地目录config目录，<br>如果还没有查找jar包内资源 */
    public static Stream<Record2<String, InputStream>> resourceStreams(ClassLoader classLoader, final String path, String... extendNames) {
        try {
            String findPath = path;
            boolean fileExists = true;
            if (!new File(path).exists()) {
                fileExists = false;
                if (!path.startsWith("/")) {
                    if (!path.startsWith("config/")) {
                        if (new File("config/" + path).exists()) {
                            findPath = "config/" + path;
                            fileExists = true;
                        }
                    }
                }
            }
            if (!fileExists) {/*当本地文件不存在才查找资源文件*/
                URL resource = classLoader.getResource(path);

                if (resource != null) {
                    if ("file".equalsIgnoreCase(resource.getProtocol())) {
                        File file1 = new File(resource.toURI());
                        findPath = file1.toPath().toString();
                    } else {
                        findPath = URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8);
                        if (findPath.contains(".zip!") || findPath.contains(".jar!")) {
                            findPath = findPath.substring(5, findPath.indexOf("!/"));
                            try (ZipReadFile zipFile = new ZipReadFile(findPath)) {
                                return zipFile.stream()
                                        .filter(z -> !z.isDirectory())
                                        .filter(p -> {
                                            String name = p.getName();
                                            if (name.startsWith(path)) {
                                                if (extendNames.length > 0) {
                                                    boolean check = false;
                                                    for (String extendName : extendNames) {
                                                        if (name.endsWith(extendName)) {
                                                            check = true;
                                                            break;
                                                        }
                                                    }
                                                    return check;
                                                }
                                            }
                                            return false;
                                        })
                                        .map(z -> new Record2<String, InputStream>(z.getName(), new ByteArrayInputStream(zipFile.unzipFile(z))))
                                        .toList()
                                        .stream();
                            }
                        }
                    }
                }
                if (findPath.startsWith("/")) {
                    findPath = findPath.substring(1);
                }
            }
            return walkFiles(findPath, extendNames).map(filePath -> {
                try {
                    return new Record2<>(filePath.toString(), Files.newInputStream(filePath));
                } catch (Exception e) {
                    throw Throw.of("resources:" + filePath, e);
                }
            });
        } catch (Exception e) {
            throw Throw.of("resources:" + path, e);
        }
    }

    /** 获取文件名 */
    public static String fileName(File file) {
        String fileName = file.getName();
        int indexOf = fileName.indexOf(".");
        return fileName.substring(0, indexOf);
    }

    /** 获取扩展名 */
    public static String extendName(File file) {
        return extendName(file.getName());
    }

    /** 获取扩展名 */
    public static String extendName(String fileName) {
        int indexOf = fileName.indexOf("?");
        if (indexOf >= 0) {
            fileName = fileName.substring(0, indexOf);
        }
        fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
        return fileName;
    }

    public static boolean exists(String fileName) {
        return new File(fileName).exists();
    }

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /** 下载网络资源 */
    public static void downloadFile(String url, String saveFileName) {
        try {
            long millis = System.currentTimeMillis();
            URL url1 = URI.create(url).toURL();
            try (InputStream inputStream = url1.openStream()) {
                File file = new File(saveFileName);
                FileWriteUtil.fileOutputStream(
                        file,
                        false,
                        outputStream -> FileReadUtil.readBytes(outputStream, inputStream)
                );
                long costTime = System.currentTimeMillis() - millis;
                log().info("下载文件：{}, 保存：{}, 大小：{} kb, 耗时：{} ms", url, saveFileName, file.length() / 1024f, costTime);
            }
        } catch (Exception e) {
            throw Throw.of(url, e);
        }
    }

    public static File createFile(String fileName) {
        return createFile(new File(fileName));
    }

    public static File createFile(File file) {
        return createFile(file, false);
    }

    /**
     * 创建文件
     *
     * @param file  路径
     * @param fugai 覆盖文件
     * @return
     */
    public static File createFile(File file, boolean fugai) {
        try {
            if (!fugai) {
                if (file.exists()) {
                    /*如果文件已经存在，无需创建*/
                    return file;
                }
            }
            mkdirs(file);
            file.createNewFile();
            return file;
        } catch (Exception e) {
            throw Throw.of(file.getPath(), e);
        }
    }

    /**
     * 删除文件 或者 文件夹
     *
     * @param filePath 需要删除的文件或者文件夹
     */
    public static void del(String filePath) {
        del(Paths.get(filePath));
    }

    /**
     * 删除文件 或者 文件夹
     *
     * @param path 删除文件 或者 文件夹
     */
    public static void del(Path path) {
        Stream<Path> walk = walk(path);
        walk.forEach(f -> {
            try {
                boolean delete = Files.deleteIfExists(f);
                log().info("删除 {}：{}, {}", Files.isDirectory(f) ? "文件夹" : "文件", f, delete);
            } catch (Exception ignore) {}
        });
    }

    /**
     * 创建文件夹
     *
     * @param fileName 需要创建文件夹的名字
     */
    public static void mkdirs(String fileName) {
        mkdirs(new File(fileName));
    }

    /**
     * 创建文件夹
     *
     * @param file 需要创建文件夹
     */
    public static void mkdirs(File file) {
        final File absoluteFile = file.getAbsoluteFile();
        if (absoluteFile.isDirectory()) {
            absoluteFile.mkdirs();
        } else {
            File parentFile = absoluteFile.getParentFile();
            if (parentFile != null)
                parentFile.mkdirs();
        }
    }

    /** 所有的文件夹 */
    public static Stream<Path> walkDirs(String path, String... extendNames) {
        return walkDirs(path, Integer.MAX_VALUE, extendNames);
    }

    /** 所有的文件 */
    public static Stream<Path> walkDirs(String path, int maxDepth, String... extendNames) {
        return walk(path, maxDepth, extendNames).filter(Files::isDirectory);
    }

    /** 所有的文件 */
    public static Stream<Path> walkFiles(String path, String... extendNames) {
        return walkFiles(path, Integer.MAX_VALUE, extendNames);
    }

    /** 所有的文件 */
    public static Stream<Path> walkFiles(Path path, String... extendNames) {
        return walkFiles(path, Integer.MAX_VALUE, extendNames);
    }

    /** 所有的文件 */
    public static Stream<Path> walkFiles(String path, int maxDepth, String... extendNames) {
        return walk(path, maxDepth, extendNames).filter(Files::isRegularFile);
    }

    public static Stream<Path> walkFiles(Path path, int maxDepth, String... extendNames) {
        return walk(path, maxDepth, extendNames).filter(Files::isRegularFile);
    }

    /** 查找所有文件, 文件夹 */
    public static Stream<Path> walk(String path, String... extendNames) {
        return walk(path, Integer.MAX_VALUE, extendNames);
    }

    /**
     * 查找文件, 文件夹
     *
     * @param path     路径
     * @param maxDepth 深度 当前目录是1
     * @return
     */
    public static Stream<Path> walk(String path, int maxDepth, String... extendNames) {
        return walk(Paths.get(path), maxDepth, extendNames);
    }

    /** 查找所有文件, 文件夹 */
    public static Stream<Path> walk(Path path, String... extendNames) {
        return walk(path, Integer.MAX_VALUE, extendNames);
    }

    /**
     * 查找文件, 文件夹
     *
     * @param path     路径
     * @param maxDepth 深度 当前目录是1
     * @return
     */
    public static Stream<Path> walk(Path path, int maxDepth, String... extendNames) {
        if (!Files.exists(path))
            return Stream.of();
        try {
            Stream<Path> walk = Files.walk(path, maxDepth);
            if (extendNames.length > 0) {
                walk = walk.filter(f -> {
                    boolean check = false;
                    for (String extendName : extendNames) {
                        if (f.toString().endsWith(extendName)) {
                            check = true;
                            break;
                        }
                    }
                    return check;
                });
            }
            return walk;
        } catch (Exception e) {
            throw Throw.of(path.toString(), e);
        }
    }

}
