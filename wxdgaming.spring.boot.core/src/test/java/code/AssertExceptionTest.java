package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import wxdgaming.spring.boot.core.lang.AssertException;

@Slf4j
public class AssertExceptionTest {

    @Test
    public void t1() {
        AssertException assertException = new AssertException("1");
        log.info("d", assertException);
    }

}
