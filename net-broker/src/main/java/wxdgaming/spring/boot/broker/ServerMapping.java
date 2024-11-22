package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.net.SocketSession;

import java.util.Set;

/**
 * 服务器映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-22 20:54
 **/
@Getter
public class ServerMapping {

    private final InnerMessage.Stype stype;
    private final int sid;
    @Setter private SocketSession session;
    @Setter private Set<Integer> listenIdSet = Set.of();

    public ServerMapping(InnerMessage.Stype stype, int sid) {
        this.stype = stype;
        this.sid = sid;
    }

    @Override public String toString() {
        return "ServerMapping{" +
               "stype=" + stype +
               ", sid=" + sid +
               ", session=" + session +
               '}';
    }
}
