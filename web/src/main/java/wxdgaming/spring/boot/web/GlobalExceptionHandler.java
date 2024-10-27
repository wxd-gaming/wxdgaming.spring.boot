package wxdgaming.spring.boot.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.lang.RunResult;

/**
 * 统一异常管理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-23 11:49
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler implements InitPrint {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RunResult> handleException(Exception ex) {
        RunResult error = RunResult.error(500, "Internal Server Error: " + ex.getMessage());
        if (ex instanceof NoResourceFoundException
            || ex instanceof ClientAbortException) {
            log.warn("{}", ex.toString());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } else {
            log.error("", ex);
        }
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

}
