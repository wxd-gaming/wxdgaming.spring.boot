package wxdgaming.spring.boot.core;

import jakarta.annotation.PostConstruct;

/**
 * 初始化打印
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 13:25
 **/
public interface InitPrint {

    @PostConstruct
    default void init() {
        LogbackUtil.logger().debug("\n{}\n", this.getClass().getName());
    }

}
