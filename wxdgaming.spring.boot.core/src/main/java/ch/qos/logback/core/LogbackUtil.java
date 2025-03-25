package ch.qos.logback.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.core.lang.Record2;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

/**
 * 测试logback动态配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-01-27 14:28
 **/
public class LogbackUtil {

    public static void resetLogback(String userDir) throws JoranException {
        resetLogback(Thread.currentThread().getContextClassLoader(), userDir);
    }

    public static void resetLogback(ClassLoader classLoader, String userDir) throws JoranException {

        if (userDir != null && !userDir.isBlank() && !userDir.isEmpty()) {
            if (!userDir.endsWith("/")) {
                userDir += "/";
            }
        }

        System.setProperty("LOG_PATH", "");
        // 加载logback.xml配置文件
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.putProperty("LOG_PATH", userDir);
        Record2<Path, byte[]> inputStream = FileUtil.findInputStream(classLoader, "logback.xml");
        if (inputStream == null) {
            inputStream = FileUtil.findInputStream(classLoader, "logback-test.xml");
        }
        configurator.doConfigure(new ByteArrayInputStream(inputStream.t2()));
        LoggerFactory.getLogger("root").info("--------------- init end ---------------");
    }

    /** 强制设置logback 配置目录 */
    public static void setLogbackConfig() {
        String key = "logback.configurationFile";
        if (System.getProperty(key) == null) {
            Path path = FileUtil.findPath("logback.xml");
            if (path != null && !(path.toString().contains("jar") && path.toString().contains("!"))) {
                /*强制设置logback的目录位置*/
                String canonicalPath = FileUtil.getCanonicalPath(path.toFile());
                System.setProperty(key, canonicalPath);
                System.out.println("logback configuration " + canonicalPath);
            }
        }
    }

    /** 重设日志级别 */
    public static String refreshLoggerLevel() {
        Level lv;
        Logger root = LoggerFactory.getLogger("root");
        if (root.isDebugEnabled()) {
            lv = Level.INFO;
        } else {
            lv = Level.DEBUG;
        }
        refreshLoggerLevel("", lv);
        return lv.toString();
    }

    /** 重设日志级别 */
    public static void refreshLoggerLevel(Level loggerLevel) {
        refreshLoggerLevel("", loggerLevel);
    }

    /**
     * 重设日志级别
     *
     * @param loggerPackage
     * @param loggerLevel
     */
    public static void refreshLoggerLevel(String loggerPackage, Level loggerLevel) {
        // #1.get logger context
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        // #2.filter the Logger object
        loggerContext.getLoggerList()
                .stream()
                .filter(a -> loggerPackage == null || loggerPackage.isEmpty() || loggerPackage.isBlank() || a.getName().startsWith(loggerPackage))
                .forEach((logger) -> logger.setLevel(loggerLevel));
    }

    public static void info(String str) {
        logger(3).info(str);
    }

    public static Logger logger() {
        return logger(3);
    }

    public static Logger logger(int stack) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stack >= stackTrace.length) {
            stack = stackTrace.length - 1;
        }
        StackTraceElement stackTraceElement = stackTrace[stack];
        return LoggerFactory.getLogger(stackTraceElement.getClassName());
    }




}
