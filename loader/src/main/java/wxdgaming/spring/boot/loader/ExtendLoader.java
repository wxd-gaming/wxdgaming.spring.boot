package wxdgaming.spring.boot.loader;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 扩展加载器,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-23 16:05
 **/
@Getter
@Setter
public class ExtendLoader extends URLClassLoader {

    private String[] extendPackages;

    public ExtendLoader(ClassLoader parent, URL[] urls) {
        super(urls, parent);
    }

    public boolean isExtendPackage(String className) {
        if (extendPackages == null) {
            return false;
        }
        for (int i = 0; i < extendPackages.length; i++) {
            String extendPackage = extendPackages[i];
            if (className.startsWith(extendPackage)) {
                return true;
            }
        }
        return false;
    }

    @Override public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isExtendPackage(name)) {
            System.out.println("ExtendLoader loadClass: " + name);
        }
        return super.loadClass(name);
    }
}
