package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentTable;

/**
 * 数据中心
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-22 19:45
 **/
@Slf4j
@Getter
@Service
public class DataCenter {

    private final ConcurrentTable<InnerMessage.Stype, Integer, ServerMapping> sessions = new ConcurrentTable<>();

}
