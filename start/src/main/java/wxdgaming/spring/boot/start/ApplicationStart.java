package wxdgaming.spring.boot.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.data.batis.DataBatisScan;
import wxdgaming.spring.boot.data.excel.DataExcelScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 19:54
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                SpringUtil.class,
                DataBatisScan.class,
                DataRedisScan.class,
                DataExcelScan.class
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                MongoAutoConfiguration.class
        }
)
public class ApplicationStart {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationStart.class, args);
    }

}
