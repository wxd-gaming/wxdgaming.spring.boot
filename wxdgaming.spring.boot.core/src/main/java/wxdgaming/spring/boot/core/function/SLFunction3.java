package wxdgaming.spring.boot.core.function;

/**
 * 有3参数。有返回值
 * </p>
 * 实例::方法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-20 19:27
 **/
@FunctionalInterface
public interface SLFunction3<P1, P2, P3, R> extends SerializableLambda {

    R apply(P1 p1, P2 p2, P3 p3) throws Exception;

}
