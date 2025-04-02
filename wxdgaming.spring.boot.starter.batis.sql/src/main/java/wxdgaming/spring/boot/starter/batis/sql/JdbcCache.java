package wxdgaming.spring.boot.starter.batis.sql;

import wxdgaming.spring.boot.starter.core.reflect.ReflectContext;
import wxdgaming.spring.boot.starter.core.cache2.Cache;
import wxdgaming.spring.boot.starter.core.cache2.LRUCache;
import wxdgaming.spring.boot.starter.batis.BaseEntity;

import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-25 10:09
 **/
public class JdbcCache<ID, V extends BaseEntity<ID>> {

    JdbcContext jdbcContext;
    Cache<ID, V> cache;
    final Class<V> tClass;

    /**
     * 缓存基类
     *
     * @param jdbcContext       数据库连接
     * @param expireAfterAccess 缓存失效事件, 单位：分钟
     */
    public JdbcCache(JdbcContext jdbcContext, int expireAfterAccess) {
        this.tClass = ReflectContext.getTClass(this.getClass(), 1);
        this.jdbcContext = jdbcContext;
        this.init(expireAfterAccess);
    }

    public JdbcCache(JdbcContext jdbcContext, Class<V> tClass, int expireAfterAccess) {
        this.tClass = tClass;
        this.jdbcContext = jdbcContext;
        this.init(expireAfterAccess);
    }

    protected void init(int expireAfterAccess) {
        cache = LRUCache.<ID, V>builder()
                .cacheName(this.getClass().getSimpleName())
                .expireAfterReadMs(TimeUnit.MINUTES.toMillis(expireAfterAccess))
                .heartTimeMs(TimeUnit.MINUTES.toMillis(1))
                .loader(this::loader)
                .heartListener(this::heartListener)
                .removalListener(this::removalListener)
                .build();
        cache.start();
    }

    protected V loader(ID key) {
        return jdbcContext.find(tClass, key);
    }

    protected void heartListener(ID key, V value) {
        jdbcContext.save(value);
    }

    protected boolean removalListener(ID key, V value) {
        jdbcContext.save(value);
        return true;
    }


    /** 如果获取数据null 抛出异常 */
    public V get(ID key) {
        return cache.get(key);
    }

    /** 获取数据，如果没有数据返回null */
    public V getIfPresent(ID ID) {
        return cache.getIfPresent(ID);
    }

    public void put(ID key, V value) {
        jdbcContext.save(value);
        cache.put(key, value);
    }

    /** 强制缓存过期 */
    public void invalidate(ID key) {
        cache.invalidate(key);
    }

}
