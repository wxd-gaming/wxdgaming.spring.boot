package wxdgaming.spring.boot.core.lang;


import wxdgaming.spring.boot.core.Throw;

/**
 * 断言异常， 此异常不包含堆栈的
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-28 14:38
 */
public class AssertException extends RuntimeException {

    public AssertException(String message) {
        this(message, Throw.StackTraceEmpty);
    }

    public AssertException(String message, StackTraceElement[] stackTrace) {
        super(message);
        setStackTrace(stackTrace);
    }

    @Override public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override public String toString() {
        return getMessage();
    }

}
