package wxdgaming.spring.boot.core.threading;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 守护线程
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-24 20:45
 **/
@Slf4j
class GuardThread extends Thread {

    static final GuardThread ins = new GuardThread();

    public GuardThread() {
        super("Guard-Thread");
        this.setDaemon(true);
        this.start();
    }

    final ConcurrentHashMap<Thread, Event> eventMap = new ConcurrentHashMap<>();

    @Override public void run() {
        while (!this.isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                this.interrupt();
            }
            StringBuilder sb = new StringBuilder();
            eventMap.forEach((thread, event) -> {
                event.check(sb, thread);
            });
            if (!sb.isEmpty()) {
                log.error(sb.toString());
            }
        }
    }
}
