package wxdgaming.spring.boot.core.function;

/**
 * 传递两个参数的消费类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-10 10:25
 **/
@FunctionalInterface
public interface FunctionE2<T1, T2, R> extends SerializableLambda {

    R apply(T1 t1, T2 t2) throws Exception;

}
