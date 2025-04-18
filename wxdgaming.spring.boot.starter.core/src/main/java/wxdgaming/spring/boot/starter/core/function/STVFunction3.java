package wxdgaming.spring.boot.starter.core.function;

/**
 * 没有返回值，有参数的方法
 * </p>
 * 类::方法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-20 19:27
 **/
@FunctionalInterface
public interface STVFunction3<T, P1, P2, P3> extends SerializableLambda {

    void apply(P1 p1, P2 p2, P3 p3) throws Exception;

}
