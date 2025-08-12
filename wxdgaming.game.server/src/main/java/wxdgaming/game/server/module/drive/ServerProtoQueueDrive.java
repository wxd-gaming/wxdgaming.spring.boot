package wxdgaming.game.server.module.drive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.MapKey;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.spring.boot.core.CoreProperties;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.net.pojo.ProtoListenerTrigger;
import wxdgaming.spring.boot.net.pojo.ServerProtoFilter;

/**
 * 消息队列
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-08 10:34
 **/
@Slf4j
@Component
public class ServerProtoQueueDrive implements ServerProtoFilter {


    final DataCenterService dataCenterService;
    private final CoreProperties coreProperties;

    public ServerProtoQueueDrive(DataCenterService dataCenterService, CoreProperties coreProperties) {
        this.dataCenterService = dataCenterService;
        this.coreProperties = coreProperties;
    }

    @Override
    public boolean doFilter(ProtoListenerTrigger protoListenerTrigger) {
        ClientSessionMapping clientSessionMapping = ThreadContext.context("clientSessionMapping");
        if (clientSessionMapping != null && clientSessionMapping.getRid() > 0) {
            Player player = dataCenterService.getPlayer(clientSessionMapping.getRid());
            if (StringUtils.isBlank(protoListenerTrigger.queueName())) {
                protoListenerTrigger.setQueueName("player-drive-" + (player.getUid() % coreProperties.getLogic().getCoreSize()));
            } else if ("map-drive".equalsIgnoreCase(protoListenerTrigger.queueName())) {
                MapKey mapKey = player.getMapKey();

            }
        }
        return true;
    }

}
