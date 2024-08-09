package wxdgaming.spring.boot.core.util;


import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * 断言辅助
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-31 09:34
 */
public class AssertUtil {

    public static class AssertException extends RuntimeException {

        public AssertException(String message, StackTraceElement[] stackTrace) {
            super(message);
            setStackTrace(stackTrace);
        }
    }

    public static AssertException assertException(String message) {
        return assertException(4, message);
    }

    public static AssertException assertException(int stackIndex, String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        stackTrace = Arrays.copyOfRange(stackTrace, stackIndex, stackTrace.length);
        return new AssertException(message, stackTrace);
    }

    /** 条件如果是false 抛出异常 */
    public static void assertTrue(boolean success) {
        if (!success) throw assertException("检查异常");
    }

    /** 条件如果是false 抛出异常 */
    public static void assertTrue(boolean success, String message) {
        if (!success) throw assertException(message);
    }

    /** 条件如果是false 抛出异常 */
    public static void assertEquals(Object o1, Object o2, String message) {
        if (Objects.equals(o1, o2)) throw assertException(message);
    }

    /** 条件如果是false 抛出异常 */
    public static void assertNotEquals(Object o1, Object o2, String message) {
        if (!Objects.equals(o1, o2)) throw assertException(message);
    }

    /** 参数是 null 抛出异常 */
    public static void assertNull(Object object) {
        if (object == null) {
            throw assertException("参数 null");
        }
    }

    /** 参数是 null 抛出异常 */
    public static String assertNotNull(String object) {
        if (object == null) {
            throw assertException("参数 null");
        }
        return object;
    }

    public void assertNullEmpty(Object source, String message) {
        if (source == null
                || (source instanceof String str && (str.isEmpty() || str.isBlank()))
                || (source instanceof Collection && ((Collection<?>) source).isEmpty())) {
            throw assertException(message);
        }
    }

}