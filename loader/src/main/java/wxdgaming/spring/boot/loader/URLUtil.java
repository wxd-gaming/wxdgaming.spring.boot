package wxdgaming.spring.boot.loader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * url处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-23 20:06
 **/
public class URLUtil {

    public static List<URL> javaClassPathList() {
        ArrayList<URL> arrayList = new ArrayList<>();
        String[] split = System.getProperty("java.class.path").split(File.pathSeparator);
        for (int i = 0; i < split.length; i++) {
            String path = split[i];
            try {
                arrayList.add(new File(path).toURI().toURL());
            } catch (Exception e) {
                throw new RuntimeException(path, e);
            }
        }
        return arrayList;
    }

    public static URL[] javaClassPaths() {
        String[] split = System.getProperty("java.class.path").split(File.pathSeparator);
        URL[] array = new URL[split.length];
        for (int i = 0; i < split.length; i++) {
            String path = split[i];
            try {
                array[i] = new File(path).toURI().toURL();
            } catch (Exception e) {
                throw new RuntimeException(path, e);
            }
        }
        return array;
    }

    public static List<URL> stringsToURLList(String... paths) {
        ArrayList<URL> arrayList = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            try {
                arrayList.add(new File(path).toURI().toURL());
            } catch (Exception e) {
                throw new RuntimeException(path, e);
            }
        }
        return arrayList;
    }

    public static URL[] stringsToURLs(String... paths) {
        URL[] array = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            try {
                array[i] = new File(path).toURI().toURL();
            } catch (Exception e) {
                throw new RuntimeException(path, e);
            }
        }
        return array;
    }

}
