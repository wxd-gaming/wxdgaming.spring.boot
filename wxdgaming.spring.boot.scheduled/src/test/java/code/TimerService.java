package code;

import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

/**
 * 定时器服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-30 11:12
 **/
@Component
public class TimerService {

    @Scheduled
    public void test() {
        System.out.println("定时器服务");
    }

}
