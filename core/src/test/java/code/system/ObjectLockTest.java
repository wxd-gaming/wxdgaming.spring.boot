package code.system;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.core.system.ObjectLock;
import wxdgaming.spring.boot.core.threading.LogicExecutor;
import wxdgaming.spring.boot.core.threading.VirtualExecutor;

/**
 * 字符串锁
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-15 21:02
 **/
public class ObjectLockTest {

    LogicExecutor logicExecutor;
    VirtualExecutor virtualExecutor;

    @Before
    public void before() {
        logicExecutor = new LogicExecutor(10);
        virtualExecutor = new VirtualExecutor(200);
    }

    @Test
    public void instanceString() throws InterruptedException {
        String loginName01 = "loginName";
        String loginName02 = "loginName";
        System.out.println(loginName01 == loginName02);
        System.out.println(loginName01.intern() == loginName02.intern());

        String loginName1 = new String("loginName");
        String loginName2 = new String("loginName");

        System.out.println(loginName1 == loginName2);
        System.out.println(loginName1.intern() == loginName2.intern());

    }

    @Test
    public void sync() throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            String loginName = new String("loginName");
            virtualExecutor.execute(() -> {
                synchronized (loginName.intern()) {
                    lock(loginName);
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            String loginName = new String("loginName");
            logicExecutor.execute(() -> {
                synchronized (loginName.intern()) {
                    lock(loginName);
                }
            });
        }
        Thread.sleep(15000);
    }

    @SneakyThrows public void lock(String string) {
        Thread.sleep(300);
        System.out.println(System.currentTimeMillis() + " - " + Thread.currentThread().toString());
    }

    @Test
    public void lock() throws InterruptedException {

        Thread.Builder.OfVirtual ofVirtual = Thread.ofVirtual().name("", 0);
        for (int i = 0; i < 10; i++) {
            String loginName = new String("loginName");
            ofVirtual.start(() -> {
                ObjectLock.lock(loginName.intern());
                try {
                    lock(loginName);
                } finally {
                    ObjectLock.unlock(loginName.intern());
                }
            });
        }

        for (int i = 0; i < 10; i++) {
            final String loginName = new String("loginName");
            Thread thread = new Thread(
                    () -> {
                        ObjectLock.lock(loginName.intern());
                        try {
                            lock(loginName);
                        } finally {
                            ObjectLock.unlock(loginName.intern());
                        }
                    },
                    String.valueOf(i + 1)
            );
            thread.start();
        }

        Thread.sleep(15000);
    }

}
