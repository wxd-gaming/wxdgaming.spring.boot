package wxdgaming.spring.boot.loader;

import lombok.Getter;
import lombok.Setter;

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
public class BootClassLoader extends URLClassLoader {

    private ExtendLoader extendLoader;

    public BootClassLoader(ClassLoader parent, URL[] urls) {
        super(urls, parent);
    }

    @Override public void addURL(URL url) {
        super.addURL(url);
    }

    @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (extendLoader != null && extendLoader.isExtendPackage(name)) {
            return extendLoader.loadClass(name);
        }
        System.out.println("loadClass: " + name);
        return super.loadClass(name);
    }

    @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

}
