package code.thread;

import org.junit.Test;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.threading.BaseExecutor;
import wxdgaming.spring.boot.core.threading.Event;
import wxdgaming.spring.boot.core.threading.EventQueue;
import wxdgaming.spring.boot.core.threading.ThreadContext;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队列测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 16:21
 **/
public class EventQueueTest {

    @Test
    public void t0() throws IOException {
        BaseExecutor test = new BaseExecutor("test", 5, 5, 3000);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            final int index = i + 1;
            ThreadContext.putContent("1", index);
            test.execute(new Event() {
                @Override protected void onEvent() throws Throwable {
                    Thread.sleep(7000);
                    System.out.println(Thread.currentThread().getName()
                            + " - " + index
                            + " -" + start
                            + " -" + System.currentTimeMillis()
                            + " - " + ThreadContext.context("1")
                    );
                }
            });
        }
        // queueEvent.getExecutor().scheduleWithFixedDelay(() -> {
        //     System.out.println(Thread.currentThread().getName() + " - schedule -" + start + " -" + System.currentTimeMillis());
        // }, 1, 1, TimeUnit.SECONDS);
        // Thread.sleep(6000);
        System.in.read();
    }


    @Test
    public void t1() throws IOException {
        EventQueue eventQueue = new EventQueue("测试队列", "test");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            final int index = i + 1;
            ThreadContext.putContent("1", index);
            eventQueue.execute(new Event() {
                @Override protected void onEvent() throws Throwable {
                    Thread.sleep(9000);
                    System.out.println(Thread.currentThread().getName()
                            + " - " + index
                            + " -" + start
                            + " -" + System.currentTimeMillis()
                            + " - " + ThreadContext.context("1")
                    );
                }
            });
        }
        // queueEvent.getExecutor().scheduleWithFixedDelay(() -> {
        //     System.out.println(Thread.currentThread().getName() + " - schedule -" + start + " -" + System.currentTimeMillis());
        // }, 1, 1, TimeUnit.SECONDS);
        // Thread.sleep(6000);
        System.in.read();
    }

    @Test
    public void t2() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        Executor executor = CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS);
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(" 0 " + Thread.currentThread());
            return "hello " + counter.incrementAndGet();
        }, executor);
        Mono<String> mono = Mono.fromFuture(completableFuture);
        System.out.println(1);
        mono.subscribe(str -> {
            System.out.println(" 1 " + Thread.currentThread() + " - " + str);
        });
        System.out.println(2);
        mono.subscribe(str -> {
            System.out.println(" 2 " + Thread.currentThread() + " - " + str);
        });
        System.out.println(3);
        mono.subscribe(str -> {
            System.out.println(" 3 " + Thread.currentThread() + " - " + str);
        });
        Thread.sleep(1500);
    }

    @Test
    public void t3() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        Mono<String> mono = Mono.fromSupplier(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(" 0 " + Thread.currentThread() + " - " + System.currentTimeMillis());
            return "hello " + counter.incrementAndGet();
        });
        mono = mono.map(str -> str + " world");
        System.out.println(1);
        mono.subscribe(str -> {
            System.out.println(" 1 " + Thread.currentThread() + " - " + System.currentTimeMillis() + " - " + str);
        });
        System.out.println(2);
        mono.subscribe(str -> {
            System.out.println(" 2 " + Thread.currentThread() + " - " + System.currentTimeMillis() + " - " + str);
        });
        System.out.println(3);
        mono.subscribe(str -> {
            System.out.println(" 3 " + Thread.currentThread() + " - " + System.currentTimeMillis() + " - " + str);
        });
        System.out.println(4);
        Thread.sleep(500);
    }

    @Test
    public void t4() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(" 0 " + Thread.currentThread() + " - " + System.currentTimeMillis());
            return "hello " + counter.incrementAndGet();
        });
        future = future.thenApplyAsync(str -> str + " world");
        System.out.println(1);
        future.thenAccept(str -> {
            System.out.println(" 1 " + Thread.currentThread() + " - " + System.currentTimeMillis() + " - " + str);
        });
        System.out.println(2);
        future.thenAccept(str -> {
            System.out.println(" 2 " + Thread.currentThread() + " - " + System.currentTimeMillis() + " - " + str);
        });
        System.out.println(3);
        future.thenAccept(str -> {
            System.out.println(" 3 " + Thread.currentThread() + " - " + System.currentTimeMillis() + " - " + str);
        });
        Thread.sleep(1500);
    }

    @Test
    public void t5() throws Exception {

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5000);

        Mono<String> mono = Mono.fromSupplier(() -> {
            return queue.poll();
        });
        mono.subscribe(str -> System.out.println(str));
        Thread.sleep(500);
        queue.add("e");
        mono.subscribe(str -> System.out.println(str));
        queue.add("e1");
        mono.subscribe(str -> System.out.println(str));
        Thread.sleep(500);
    }

    @Test
    public void t6() throws Exception {

        Mono<String> mono = Mono.fromSupplier(() -> {
            System.out.println("0");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ignore) {}

            return "d";
        });
        System.out.println("1");
        mono.subscribe(str -> System.out.println(" 1 " + str));
        System.out.println("2");
        mono.subscribe(str -> System.out.println(" 2 " + str));
        System.out.println("3");
        mono.subscribe(str -> System.out.println(" 3 " + str));
        Thread.sleep(500);
    }

}
