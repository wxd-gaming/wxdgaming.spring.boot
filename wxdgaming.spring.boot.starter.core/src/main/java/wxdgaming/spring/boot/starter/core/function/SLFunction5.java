package wxdgaming.spring.boot.starter.core.function;

/**
 * lmd表达式
 * </p>
 * 实例::方法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-10-20 19:27
 **/
@FunctionalInterface
public interface SLFunction5<P1, P2, P3, P4, P5, R> extends SerializableLambda {

    R apply(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) throws Exception;

}
