package wxdgaming.spring.boot.starter.core.loader;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 加载器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-23 16:07
 **/
@Getter
@Setter
@Accessors(chain = true)
public class BootClassLoader extends URLClassLoader {

    private ExtendLoader extendLoader;

    public BootClassLoader() {
        super(new URL[0], Thread.currentThread().getContextClassLoader());
    }

    public BootClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public BootClassLoader(ClassLoader parent, String... paths) {
        super(URLUtil.stringsToURLArray(paths), parent);
    }

    public BootClassLoader(ClassLoader parent, URL... urls) {
        super(urls, parent);
    }

    public void addURLs(String... paths) {
        URL[] urls = URLUtil.stringsToURLArray(paths);
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            addURL(url);
        }
    }

    public void addURL(URL... urls) {
        for (int i = 0; i < urls.length; i++) {
            URL url = urls[i];
            addURL(url);
        }
    }

    @Override public void addURL(URL url) {
        super.addURL(url);
    }

    @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.loadClass(name);
        }
        return super.loadClass(name);
    }

    @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.loadClass(name, resolve);
        }
        return super.loadClass(name, resolve);
    }

    @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.findClass(name);
        }
        if (URLUtil.printLogger) {
            System.out.println("BootClassLoader findClass: " + name);
        }
        return super.findClass(name);
    }

    @Override public InputStream getResourceAsStream(String name) {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.getResourceAsStream(name);
        }
        return super.getResourceAsStream(name);
    }

    @Override public URL getResource(String name) {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.getResource(name);
        }
        return super.getResource(name);
    }


    @Override public URL findResource(String name) {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.findResource(name);
        }
        if (URLUtil.printLogger) {
            System.out.println("BootClassLoader findResource: " + name);
        }
        return super.findResource(name);
    }
}
