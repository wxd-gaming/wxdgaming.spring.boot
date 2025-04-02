package wxdgaming.spring.boot.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.starter.core.SpringReflectContent;
import wxdgaming.spring.boot.starter.core.ann.Init;
import wxdgaming.spring.boot.starter.net.SocketSession;

import java.util.List;

/**
 * 派发器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 20:01
 **/
@Slf4j
@Getter
@Component
public class ProtoListenerFactory {

    /** 相当于用 read and copy write方式作为线程安全性 */
    ProtoListenerContent protoListenerContent = null;
    IWebSocketStringListener iWebSocketStringListener = null;
    List<ProtoFilter> protoFilters;

    @Init
    @Order(6)
    public void init(SpringReflectContent runApplication) {
        protoListenerContent = new ProtoListenerContent(runApplication);
        iWebSocketStringListener = runApplication.classWithSuper(IWebSocketStringListener.class).findFirst().orElse(null);
        protoFilters = runApplication.classWithSuper(ProtoFilter.class).toList();
    }

    public SpringReflectContent getRunApplication() {
        return protoListenerContent.getRunApplication();
    }

    public int messageId(Class<? extends PojoBase> pojoClass) {
        return protoListenerContent.messageId(pojoClass);
    }

    public void dispatch(SocketSession socketSession, int messageId, byte[] data) {
        ProtoMapping mapping = protoListenerContent.getMappingMap().get(messageId);
        if (mapping == null) {
            throw new RuntimeException("未找到消息id: %s".formatted(messageId));
        }
        if (log.isDebugEnabled()) {
            log.debug("收到消息：{} {} {}", socketSession, messageId, mapping.pojoClass().getSimpleName());
        }
        ProtoListenerTrigger protoListenerTrigger = new ProtoListenerTrigger(mapping, protoListenerContent.getRunApplication(), socketSession, messageId, data);
        boolean allMatch = protoFilters.stream()
                .allMatch(filter -> filter.doFilter(protoListenerTrigger, socketSession, protoListenerTrigger.getPojoBase()));
        if (!allMatch) {
            return;
        }
        protoListenerTrigger.submit();
    }

}
