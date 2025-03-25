package wxdgaming.spring.boot.core.function;

/**
 * 创建
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-25 15:11
 **/
public interface Factory1<T1, R> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    R get(T1 t1) throws Exception;
}
