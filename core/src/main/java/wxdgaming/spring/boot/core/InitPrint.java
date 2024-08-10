package wxdgaming.spring.boot.core;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 初始化打印
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 13:25
 **/
public interface InitPrint {

    @PostConstruct
    default void init(){
        System.out.println("\n" + this.getClass().getName() + "\n");
    }

}
