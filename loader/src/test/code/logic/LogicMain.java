package logic;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

/**
 * 逻辑入口
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-23 16:34
 **/
public class LogicMain {

    public static void init(int sid) {
        String userDir = String.valueOf(sid);
        System.setProperty("logic", userDir);
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
        LogicTest logicTest = new LogicTest(sid);
    }

}
