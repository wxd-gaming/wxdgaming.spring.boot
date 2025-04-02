package wxdgaming.spring.boot.starter.core.cache2;

import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.format.data.Data2Size;
import wxdgaming.spring.boot.starter.core.threading.Event;
import wxdgaming.spring.boot.starter.core.threading.ExecutorUtil;
import wxdgaming.spring.boot.starter.core.threading.ExecutorUtilImpl;
import wxdgaming.spring.boot.starter.core.threading.TimerJob;
import wxdgaming.spring.boot.starter.core.timer.MyClock;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * lru 类型的缓存, 仅有key
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-20 16:14
 **/
@Slf4j
@SuperBuilder
public class LRUKeyCache<K> extends Cache<K, Boolean> {

    List<CacheLock> reentrantLocks;
    List<HashMap<K, CacheHolderNone>> nodes;

    /** 计算内存大小 注意特别耗时，并且可能死循环 */
    @Deprecated
    public long memorySize() {
        long size = 0;
        for (HashMap<K, CacheHolderNone> node : nodes) {
            size += Data2Size.totalSize0(node);
        }
        return size;
    }

    CacheHolderNone newCacheHolder(Boolean value) {
        CacheHolderNone cacheHolder = new CacheHolderNone();
        cacheHolder.setLastHeartTime(MyClock.millis() + heartTimeMs);
        if (this.expireAfterWriteMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterWriteMs);
        }
        return cacheHolder;
    }

    void refresh(CacheHolderNone cacheHolder) {
        if (this.expireAfterReadMs > 0) {
            cacheHolder.setExpireEndTime(MyClock.millis() + expireAfterReadMs);
        }
    }

    public void start() {
        super.start();
        initNodes();
        this.timerJobs = new TimerJob[this.area];

        for (int i = 0; i < this.area; i++) {
            final int hashIndex = i;
            Event heartEvent = new Event(500, 1000) {
                @Override public void onEvent() throws Exception {
                    CacheLock cacheLock = reentrantLocks.get(hashIndex);
                    cacheLock.writeLock.lock();
                    try {
                        Iterator<Map.Entry<K, CacheHolderNone>> iterator = nodes.get(hashIndex).entrySet().iterator();
                        long millis = MyClock.millis();
                        while (iterator.hasNext()) {
                            Map.Entry<K, CacheHolderNone> next = iterator.next();
                            CacheHolderNone holder = next.getValue();
                            if (millis > holder.getExpireEndTime()) {
                                boolean remove = true;
                                if (LRUKeyCache.this.removalListener != null) {
                                    remove = LRUKeyCache.this.removalListener.apply(next.getKey(), true);
                                }
                                if (remove)
                                    iterator.remove();
                                else
                                    refresh(holder);/*移除缓存失败刷新一次*/
                            } else {
                                if (LRUKeyCache.this.heartListener != null && millis > holder.getLastHeartTime()) {
                                    LRUKeyCache.this.heartListener.accept(next.getKey(), true);
                                }
                            }
                        }
                    } finally {
                        cacheLock.writeLock.unlock();
                    }
                }
            };
            TimerJob timerJob = ExecutorUtilImpl.getInstance()
                    .getBasicExecutor()
                    .scheduleAtFixedDelay(
                            heartEvent,
                            this.heartTimeMs,
                            this.heartTimeMs,
                            TimeUnit.MILLISECONDS
                    );
            this.timerJobs[i] = timerJob;
        }
    }

    private void initNodes() {
        List<CacheLock> tmpLock = new ArrayList<>(this.area);
        List<HashMap<K, CacheHolderNone>> tmpNodes = new ArrayList<>(this.area);
        for (int i = 0; i < this.area; i++) {
            tmpLock.add(new CacheLock());
            tmpNodes.add(new HashMap<>());
        }

        this.reentrantLocks = Collections.unmodifiableList(tmpLock);
        this.nodes = Collections.unmodifiableList(tmpNodes);
    }

    @Override public void shutdown() {
        for (TimerJob timerJob : timerJobs) {
            timerJob.cancel();
        }
        invalidateAll();
    }

    @Override public boolean has(K k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            return nodes.get(hashIndex).containsKey(k);
        } finally {
            cacheLock.readLock.unlock();
        }
    }

    @Override public Boolean get(K k) throws NullPointerException {
        Boolean ifPresent = getIfPresent(k);
        if (ifPresent == null) {
            throw new NullPointerException(String.format("cache key=%s value is null", k));
        }
        return ifPresent;
    }

    @Override public Boolean getIfPresent(K k) {
        int hashIndex = hashIndex(k);
        CacheHolderNone cacheHolder = null;
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.readLock.lock();
        try {
            cacheHolder = nodes.get(hashIndex).get(k);
        } finally {
            cacheLock.readLock.unlock();
        }
        if (cacheHolder == null) {
            cacheLock.writeLock.lock();
            try {
                cacheHolder = nodes.get(hashIndex).get(k);
                if (cacheHolder == null) {
                    /*双重锁确保正确命中*/
                    if (LRUKeyCache.this.loader == null)
                        return null;
                    Boolean apply = LRUKeyCache.this.loader.apply(k);
                    if (apply == null)
                        return null;
                    cacheHolder = newCacheHolder(apply);
                    nodes.get(hashIndex).put(k, cacheHolder);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
        if (cacheHolder == null) return null;
        refresh(cacheHolder);
        return true;
    }

    @Override public Boolean put(K k, Boolean v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderNone cacheHolder = newCacheHolder(v);
            CacheHolderNone old = nodes.get(hashIndex).put(k, cacheHolder);
            return old != null;
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public Boolean putIfAbsent(K k, Boolean v) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderNone cacheHolder = newCacheHolder(v);
            CacheHolderNone old = nodes.get(hashIndex).putIfAbsent(k, cacheHolder);
            return old != null;
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public Boolean invalidate(K k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            CacheHolderNone cacheHolder = nodes.get(hashIndex).get(k);
            if (cacheHolder == null) {
                return null;
            }
            cacheHolder.setExpireEndTime(0);
            return true;
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Override public void invalidateAll() {
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.writeLock.lock();
            try {
                HashMap<K, CacheHolderNone> node = nodes.get(i);
                for (CacheHolderNone holder : node.values()) {
                    holder.setExpireEndTime(0);
                }
            } finally {
                cacheLock.writeLock.unlock();
            }
        }
    }

    @Override public Collection<K> keys() {
        List<K> result = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                HashMap<K, CacheHolderNone> node = nodes.get(i);
                List<K> tmp = new ArrayList<>(node.size());
                for (Map.Entry<K, CacheHolderNone> holderLongEntry : node.entrySet()) {
                    tmp.add(holderLongEntry.getKey());
                }
                result.addAll(tmp);
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return result;
    }

    /** 拷贝所有元素 */
    @Override public Collection<Boolean> values() {
        throw new UnsupportedOperationException("不支持");
    }

    @Override public long size() {
        long size = 0;
        for (int i = 0; i < nodes.size(); i++) {
            CacheLock cacheLock = reentrantLocks.get(i);
            cacheLock.readLock.lock();
            try {
                HashMap<K, CacheHolderNone> node = nodes.get(i);
                size += node.size();
            } finally {
                cacheLock.readLock.unlock();
            }
        }
        return size;
    }

    @Deprecated
    @Override public void discard(K k) {
        int hashIndex = hashIndex(k);
        CacheLock cacheLock = reentrantLocks.get(hashIndex);
        cacheLock.writeLock.lock();
        try {
            nodes.get(hashIndex).remove(k);
        } finally {
            cacheLock.writeLock.unlock();
        }
    }

    @Deprecated
    @Override public void discardAll() {
        initNodes();
    }


}
