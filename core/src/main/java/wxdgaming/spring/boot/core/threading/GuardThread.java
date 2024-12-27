package wxdgaming.spring.boot.core.threading;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.lang.DiffTime;
import wxdgaming.spring.boot.core.lang.Tick;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 守护线程
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-15 09:06
 **/
@Slf4j
class GuardThread implements Runnable, InitPrint, Closeable {

    @Getter static final GuardThread ins = new GuardThread();

    private final Thread thread;
    private final ConcurrentHashMap<Thread, Record> cacheMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Thread, Record> currentRunnableMap = new ConcurrentHashMap<>();

    public GuardThread() {
        this.thread = new Thread(this, "守护线程");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    @Override public void close() throws IOException {
        this.thread.interrupt();
    }

    public void push(Event runnable) {
        Record record = cacheMap.computeIfAbsent(Thread.currentThread(), k -> new Record());
        record.reset(runnable);
        currentRunnableMap.put(Thread.currentThread(), record);
    }

    public void release() {
        currentRunnableMap.remove(Thread.currentThread());
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(500);
                /*删除无效的*/
                cacheMap.entrySet().removeIf(v -> v.getKey().isInterrupted());
                for (Map.Entry<Thread, Record> entry : currentRunnableMap.entrySet()) {
                    Thread thread = entry.getKey();
                    Record tuple = entry.getValue();
                    check(thread, tuple);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception ignore) {}
        }
    }

    void check(Thread thread, Record tuple) {
        if (!tuple.tick.need()) {
            return;
        }
        float diff = tuple.diffTime.diffLong() / 100 / 10f;
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("线程：").append(thread.getName()).append("\n");
        if (StringsUtil.notEmptyOrNull(tuple.runnable.queueName)) {
            sb.append("队列：").append(tuple.runnable.queueName).append("\n");
        }
        sb.append("任务：").append(tuple.runnable.runName).append("\n");
        sb.append("耗时：").append(String.format("%.2f", diff)).append(" s\n");
        Throw.ofString(sb, thread.getStackTrace(), true);
        log.warn(sb.toString());
    }

    static class Record {

        final DiffTime diffTime = new DiffTime();
        final Tick tick = new Tick(50, 3000, 0);
        Event runnable;

        public void reset(Event runnable) {
            this.tick.reset();
            this.diffTime.reset();
            this.runnable = runnable;
        }

    }

}
