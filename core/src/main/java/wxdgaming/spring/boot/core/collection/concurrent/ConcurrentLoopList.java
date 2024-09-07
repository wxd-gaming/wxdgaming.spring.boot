package wxdgaming.spring.boot.core.collection.concurrent;

import wxdgaming.spring.boot.core.lang.ObjectBaseLock;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * 循环列表
 *
 * @param <T>
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 12:05
 */
public class ConcurrentLoopList<T> extends ObjectBaseLock {

    private final AtomicLong atomicLong = new AtomicLong();
    private final ArrayList<T> list = new ArrayList<>();

    public boolean add(T t) {
        lock();
        try {return list.add(t);} finally {unlock();}
    }

    public boolean remove(T t) {
        lock();
        try {return list.remove(t);} finally {unlock();}
    }

    public void forEach(Consumer<T> consumer) {
        lock();
        try {
            list.forEach(consumer);
        } finally {
            unlock();
        }
    }

    /** 拷贝一个副本 */
    public List<T> duplicate() {
        lock();
        try {return new ArrayList<>(list);} finally {unlock();}
    }

    /** 循环获取 如果 null 则会引发异常 */
    public T loopOrException() {
        T loop = loop();
        AssertUtil.assertNullEmpty(loop, "当前链接 empty ");
        return loop;
    }

    /** 循环获取 */
    public T loop() {
        lock();
        try {
            if (list.isEmpty()) return null;
            long andIncrement = atomicLong.getAndIncrement();
            if (andIncrement > Integer.MAX_VALUE) {
                atomicLong.set(0);
            }
            int index = (int) (andIncrement % list.size());
            return list.get(index);
        } finally {unlock();}
    }

}
