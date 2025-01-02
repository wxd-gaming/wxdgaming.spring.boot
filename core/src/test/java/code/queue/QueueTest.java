package code.queue;

import org.junit.Test;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-01-02 14:49
 **/
public class QueueTest {

    @Test
    public void concurrentLinkedQueueTest() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");
        queue.add("6");
        queue.add("7");
        queue.add("8");
        queue.add("9");
        queue.add("10");
    }

    @Test
    public void linkedBlockingQueueTest() {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");
        queue.add("6");
        queue.add("7");
        queue.add("8");
        queue.add("9");
        queue.add("10");
    }
}
