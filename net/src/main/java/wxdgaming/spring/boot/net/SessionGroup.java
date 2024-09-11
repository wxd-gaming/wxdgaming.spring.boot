package wxdgaming.spring.boot.net;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentLoopList;

/**
 * channel 列表
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-09 16:04
 **/
@Getter
public class SessionGroup extends ConcurrentLoopList<SocketSession> {

    protected final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override public boolean add(SocketSession session) {
        channelGroup.add(session.getChannel());
        return super.add(session);
    }

    @Override public boolean remove(SocketSession session) {
        channelGroup.remove(session.getChannel());
        return super.remove(session);
    }

    public void write(Object message) {
        channelGroup.write(message);
    }

    public void writeAndFlush(Object message) {
        channelGroup.writeAndFlush(message);
    }

    public void flush() {
        channelGroup.flush();
    }

}