package wxdgaming.spring.boot.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.lang.RunResult;

import java.io.FileNotFoundException;

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
    public ResponseEntity<?> handleException(Exception ex) {
        if (ex instanceof ClientAbortException) {
            log.warn("{}", ex.toString());
        } else if (ex instanceof NoResourceFoundException
                   || ex instanceof FileNotFoundException) {
            // 加载图片资源
            Resource resource = new UrlResource(this.getClass().getResource("/error.jpg"));
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                    .body(resource);
        } else {
            log.error("", ex);
        }
        RunResult error = RunResult.error(500, "Internal Server Error: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
