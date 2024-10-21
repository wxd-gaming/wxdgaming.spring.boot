package wxdgaming.spring.boot.data.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.timer.MyClock;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 09:25
 **/
@Slf4j
@Service
@ComponentScan
@ComponentScan(basePackageClasses = CoreScan.class)
@EnableScheduling
public class DataRedisScan implements InitPrint {


    @Scheduled(cron = "*/10 * * * * ?")
    public void test() {
        log.info("{}", MyClock.nowString());
    }

}
