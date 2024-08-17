package wxdgaming.spring.boot.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.data.batis.DataBatisScan;
import wxdgaming.spring.boot.data.excel.DataExcelScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.net.NetScan;
import wxdgaming.spring.boot.rpc.RpcScan;
import wxdgaming.spring.boot.web.WebScan;
import wxdgaming.spring.boot.weblua.WebLuaScan;

import java.util.Arrays;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 19:54
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                ApplicationStart.class,
                CoreScan.class,
                DataBatisScan.class,
                DataRedisScan.class,
                DataExcelScan.class,
                WebScan.class,
                WebLuaScan.class,
                NetScan.class,
                RpcScan.class,
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                MongoAutoConfiguration.class
        }
)
public class ApplicationStart {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ApplicationStart.class, args);

        SpringUtil ins = SpringUtil.getIns();
        ins.withMethodAnnotated(Start.class)
                .forEach(method -> {
                    try {
                        Object bean = ins.getBean(method.getDeclaringClass());
                        method.setAccessible(true);
                        Object[] array = Arrays.stream(method.getParameterTypes()).map(ins::getBean).toArray();
                        method.invoke(bean, array);
                    } catch (Exception e) {
                        throw new RuntimeException(method.toString(), e);
                    }
                });

    }

}
