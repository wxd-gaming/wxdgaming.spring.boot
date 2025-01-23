package wxdgaming.spring.boot.broker.spi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.broker.DataCenter;
import wxdgaming.spring.boot.broker.ServerMapping;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.net.ProtoMapping;
import wxdgaming.spring.boot.net.SocketSession;

import java.util.Set;

/**
 * 请求rpc执行处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 19:43
 **/
@Slf4j
@Controller
public class ReqBrokerRegisterController {

    final DataCenter dataCenter;

    public ReqBrokerRegisterController(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }


    @ProtoMapping
    public void reqRegisterAction(SocketSession session, InnerMessage.ReqBrokerRegister reqRegister) throws Exception {
        InnerMessage.Stype stype = reqRegister.getStype();
        int sid = reqRegister.getSid();
        session.attribute("register", reqRegister);
        ServerMapping serverMapping = dataCenter.getSessions().computeIfAbsent(stype, sid, m -> new ServerMapping(stype, sid));
        serverMapping.setSession(session);
        serverMapping.setListenIdSet(Set.copyOf(reqRegister.getListenMessageId()));
    }

}
