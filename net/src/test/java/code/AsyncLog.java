package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 异步日志测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-10 09:32
 **/
@Slf4j
public class AsyncLog {

    @Test
    public void test() throws Exception {
       String tmp= "s".transform(s -> s + "d");
        for (int k = 0; k < 10; k++) {
            long nanoTime = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                log.info("test {}", i);
            }
            System.out.println("耗时：" + (System.nanoTime() - nanoTime) / 10000 / 100f + "ms");
        }
        System.in.read();
    }

}
