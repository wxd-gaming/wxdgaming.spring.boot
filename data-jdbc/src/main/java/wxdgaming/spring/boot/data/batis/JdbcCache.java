package wxdgaming.spring.boot.data.batis;

import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.cache.Cache;
import wxdgaming.spring.boot.data.EntityUID;

import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-25 10:09
 **/
public class JdbcCache<ID, V extends EntityUID<ID>> {

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
        Cache.CacheBuilder<ID, V> builder = Cache.builder();
        builder.cacheName(this.getClass().getSimpleName())
                .expireAfterAccess(expireAfterAccess, TimeUnit.MINUTES)
                .delay(TimeUnit.MINUTES.toMillis(1))
                .heartTime(TimeUnit.MINUTES.toMillis(5))
                .loader(this::loader)
                .heartListener(this::heartListener)
                .removalListener(this::removalListener);
        cache = builder.build();
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

}
