package wxdgaming.spring.boot.core.function;

/**
 * 没有返回值，没有参数的方法
 * </p>
 * 类::方法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-20 19:27
 **/
@FunctionalInterface
public interface STVFunction0<T> extends SerializableLambda {

    void apply() throws Exception;

}
