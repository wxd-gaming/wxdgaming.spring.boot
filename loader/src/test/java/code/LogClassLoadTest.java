package code;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.loader.BootClassLoader;
import wxdgaming.spring.boot.loader.ExtendLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 日志组件class加载器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-23 16:12
 **/
@Slf4j
public class LogClassLoadTest {

    public static void main(String[] args) throws Exception {
        loadLogic(3);
        loadLogic(1);
        loadLogic(2);
    }

    public static void loadLogic(int sid) throws Exception {
        BootClassLoader bootClassLoader = new BootClassLoader(
                LogClassLoadTest.class.getClassLoader(),
                new URL[]{new File("F:\\log-code").toURI().toURL()}
        );
        bootClassLoader.setExtendLoader(getExtendLoader());
        Class<?> aClass = bootClassLoader.loadClass("logic.LogicMain");
        System.out.println(aClass.hashCode() + " - " + aClass.getClassLoader().hashCode());
        aClass.getMethod("init", int.class).invoke(null, sid);
    }

    public static ExtendLoader getExtendLoader() throws Exception {
        URL[] array = Files.walk(Paths.get("F:\\log-libs"), 1)//.filter(v -> v.toString().endsWith(".jar"))
                .map(p -> {
                    try {
                        return p.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(URL[]::new);
        ExtendLoader extendLoader = new ExtendLoader(null, array);
        extendLoader.setExtendPackages(new String[]{
                        "org.slf4j",
                        "ch.qos.logback.classic.Logger",
                        "org.apache.logging.log4j"
                }
        );
        return extendLoader;
    }

}
