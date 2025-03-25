package wxdgaming.spring.boot.core.function;

/**
 * 传递三个参数的消费类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-10 10:25
 **/
@FunctionalInterface
public interface Consumer6<T1, T2, T3, T4, T5, T6> extends SerializableLambda {

    void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);

}
