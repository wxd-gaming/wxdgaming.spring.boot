package wxdgaming.spring.boot.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.SocketSession;

/**
 * 请求rpc执行处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 19:43
 **/
@Slf4j
@Controller
public class ReqBrokerRegisterController {

    private final BrokerService brokerService;

    public ReqBrokerRegisterController(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @MsgMapper
    public void reqRegisterAction(SocketSession session, InnerMessage.ReqBrokerRegister reqRegister) throws Exception {
        InnerMessage.Stype stype = reqRegister.getStype();
        int sid = reqRegister.getSid();
        session.attribute("register", reqRegister);
        brokerService.getSessions().put(stype, sid, session);
    }

}
