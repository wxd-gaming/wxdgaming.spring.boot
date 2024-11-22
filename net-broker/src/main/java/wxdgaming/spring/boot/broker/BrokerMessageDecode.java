package wxdgaming.spring.boot.broker;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.*;
import wxdgaming.spring.boot.net.server.ServerMessageDecode;

/**
 * 代理解码
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-17 16:36
 **/
@ChannelHandler.Sharable
public class BrokerMessageDecode extends ServerMessageDecode implements InitPrint {

    final SessionGroup sessionGroup;
    DataCenter dataCenter;

    public BrokerMessageDecode(BootstrapBuilder bootstrapBuilder,
                               MessageDispatcher dispatcher,
                               SessionGroup sessionGroup,
                               DataCenter dataCenter) {
        super(bootstrapBuilder, dispatcher);
        this.sessionGroup = sessionGroup;
        this.dataCenter = dataCenter;
    }

    @Override protected void notSpi(SocketSession socketSession, int messageId, byte[] messageBytes) {
        Integer sid = socketSession.attribute("sid");

        ServerMapping serverMapping = dataCenter.getSessions().get(InnerMessage.Stype.GAME, sid);
        if (serverMapping != null && serverMapping.getSession() != null) {
            ByteBuf build = MessageEncode.build(messageId, messageBytes);
            /*转发消息*/
            serverMapping.getSession().writeAndFlush(build);
        }
    }

}
