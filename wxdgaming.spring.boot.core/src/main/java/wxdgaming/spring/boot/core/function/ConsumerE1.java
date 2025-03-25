package wxdgaming.spring.boot.core.function;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-08-18 15:40
 **/
@FunctionalInterface
public interface ConsumerE1<T> extends SerializableLambda {

    void accept(T t) throws Exception;

}
