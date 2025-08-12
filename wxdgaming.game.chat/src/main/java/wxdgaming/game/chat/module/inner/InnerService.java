package wxdgaming.game.chat.module.inner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentTable;
import wxdgaming.spring.boot.net.SocketSession;

/**
 * 内嵌服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-09 14:18
 **/
@Slf4j
@Component
public class InnerService {

    final ConcurrentTable<ServiceType, Integer, SocketSession> sessionTable = new ConcurrentTable<>();

}
