package wxdgaming.spring.boot.core.function;

/**
 * 使用方式 带返回值
 * </p>
 * 类::方法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-20 19:27
 **/
@FunctionalInterface
public interface STLFunction0<T, R> extends SerializableLambda {

    R apply() throws Throwable;

}
