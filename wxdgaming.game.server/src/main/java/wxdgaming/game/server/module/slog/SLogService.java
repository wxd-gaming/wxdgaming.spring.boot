package wxdgaming.game.server.module.slog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.game.server.GameServiceBootstrapConfig;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.logbus.LogBusService;
import wxdgaming.spring.logbus.LogEntity;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:17
 **/
@Slf4j
@Service
public class SLogService implements InitPrint {

    final GameServiceBootstrapConfig gameServiceBootstrapConfig;
    final LogBusService logBusService;
    final ConcurrentHashMap<String, HexId> logHexIdMap = new ConcurrentHashMap<>();

    public SLogService(GameServiceBootstrapConfig gameServiceBootstrapConfig, LogBusService logBusService) {
        this.gameServiceBootstrapConfig = gameServiceBootstrapConfig;
        this.logBusService = logBusService;
    }

    public long newLogId(String logType) {
        return logHexIdMap.computeIfAbsent(logType, (key) -> new HexId(gameServiceBootstrapConfig.getSid())).newId();
    }

    public void addLog(AbstractRoleLog abstractRoleLog) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(newLogId(abstractRoleLog.logType()));
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType(abstractRoleLog.logType());
        logEntity.getLogData().putAll(abstractRoleLog.toJSONObject());

        abstractRoleLog.setCurSid(gameServiceBootstrapConfig.getSid());

        logBusService.addLog(logEntity);
    }

    public void addLog(AbstractSLog abstractSLog) {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(newLogId(abstractSLog.logType()));
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType(abstractSLog.logType());
        logEntity.getLogData().putAll(abstractSLog.toJSONObject());

        abstractSLog.setSid(gameServiceBootstrapConfig.getSid());
        abstractSLog.setCurSid(gameServiceBootstrapConfig.getSid());

        logBusService.addLog(logEntity);
    }

}
