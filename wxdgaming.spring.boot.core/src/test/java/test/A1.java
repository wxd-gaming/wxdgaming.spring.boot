package test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.core.CoreConfig;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.executor.ExecutorService;

@SpringBootTest(classes = {
        CoreConfig.class,
        A1.class
})
class A1 {

    @Test
    void contextLoads() {
        ExecutorService executorServiceBasic = ExecutorFactory.getExecutorServiceBasic();
        executorServiceBasic.execute(() -> {
            System.out.println("hello world");
        });
    }

}
