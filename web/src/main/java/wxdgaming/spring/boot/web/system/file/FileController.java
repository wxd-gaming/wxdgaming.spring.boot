package wxdgaming.spring.boot.web.system.file;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * 文件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 17:15
 */
@Controller
@RequestMapping
public class FileController implements InitPrint {

    // @GetMapping(path = "/*")
    // public String html(HttpServletRequest httpServletRequest) {
    //     return httpServletRequest.getRequestURI().substring(1);
    // }

}
