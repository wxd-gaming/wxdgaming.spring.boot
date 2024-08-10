package wxdgaming.spring.boot.data.batis;

import org.springframework.context.annotation.ComponentScan;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 09:25
 **/
@ComponentScan
public class DataBatisScan {

    public DataBatisScan() {
        System.out.println("\n" + this.getClass().getName() + "\n");
    }

}
