package wxdgaming.spring.boot.core;

import ch.qos.logback.core.LogbackUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 添加初始化打印
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-22 09:59
 */
public interface InitPrint {

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @PostConstruct
    default void __initPrint() {
        LogbackUtil.logger().debug("init print {} {}", this.getClass().getName(), this.hashCode());
    }

}
