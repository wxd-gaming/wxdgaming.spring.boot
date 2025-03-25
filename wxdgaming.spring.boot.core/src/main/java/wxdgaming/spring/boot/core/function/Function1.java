package wxdgaming.spring.boot.core.function;

/**
 * 传递1个参数的消费类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-10 10:25
 **/
@FunctionalInterface
public interface Function1<T1, R> extends SerializableLambda {

    R apply(T1 t1);

}
