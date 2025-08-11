package run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.logbus.LogBusService;
import wxdgaming.spring.logbus.LogEntity;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-08 17:52
 **/
@Slf4j
@Service
public class PostLogService implements InitPrint {

    final LogBusService logBusService;
    static final HexId HEX_ID = new HexId(1);

    @Autowired
    public PostLogService(LogBusService logBusService) {
        this.logBusService = logBusService;
    }


    @Scheduled(cron = "*/5 * * * * ?")
    public void loginLog() {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(HEX_ID.newId());
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType("login");
        logEntity.putLogData("account", StringUtils.randomString(8));
        logBusService.addLog(logEntity);
    }

    @Scheduled(cron = "*/5 * * * * ?")
    public void rechargeLog() {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(HEX_ID.newId());
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType("recharge");
        logEntity.putLogData("openId", StringUtils.randomString(8));
        logEntity.putLogData("account", StringUtils.randomString(8));
        logEntity.putLogData("spOrderId", StringUtils.randomString(8));
        logEntity.putLogData("cpOrderId", StringUtils.randomString(8));

        logEntity.putLogData("money", 6);
        logBusService.addLog(logEntity);
    }


}
