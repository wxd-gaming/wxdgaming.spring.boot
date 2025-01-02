package code.thread;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.threading.EventCallable;
import wxdgaming.spring.boot.core.threading.EventQueue;
import wxdgaming.spring.boot.core.threading.ExecutorService;
import wxdgaming.spring.boot.core.threading.LogicExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * 执行
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-31 17:02
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = {CoreScan.class})
public class ExecutorTest {

    @Autowired LogicExecutor logicExecutor;
    EventQueue eventQueue = null;

    @PostConstruct
    public void init() {
        eventQueue = ExecutorService.getEventQueue("default");
        if (eventQueue == null)
            eventQueue = new EventQueue("default", logicExecutor);
    }

    @Test
    public void tall() {
        tExecutor();
        tQueue();
    }

    @Test
    public void tExecutor() {
        CompletableFuture<String> future1 = logicExecutor.submit(new EventCallable<String>() {
            @Override public String call() throws Exception {
                Thread.sleep(5);
                return "Executor submit 1";
            }
        });


        CompletableFuture<String> future2 = logicExecutor.submit(new EventCallable<String>() {
            @Override public String call() throws Exception {
                Thread.sleep(5);
                return "Executor submit 2";
            }
        });

        future1.thenAccept(str -> log.info("{}", str));
        future2.thenAccept(str -> log.info("{}", str));

        CompletableFuture.allOf(future1, future2).join();
    }

    @Test
    public void tQueue() {

        CompletableFuture<String> future1 = eventQueue.submit(new EventCallable<String>() {
            @Override public String call() throws Exception {
                Thread.sleep(5);
                return "eventQueue submit 1";
            }
        });


        CompletableFuture<String> future2 = eventQueue.submit(new EventCallable<String>() {
            @Override public String call() throws Exception {
                Thread.sleep(5);
                return "eventQueue submit 2";
            }
        });

        future1.thenAccept(str -> log.info("{}", str));
        future2.thenAccept(str -> log.info("{}", str));

        CompletableFuture.allOf(future1, future2).join();
    }

}
