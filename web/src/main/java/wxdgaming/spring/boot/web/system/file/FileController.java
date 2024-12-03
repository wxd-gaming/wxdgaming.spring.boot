package wxdgaming.spring.boot.web.system.file;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * 文件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-10 17:15
 */
@Order(2)
@Controller
public class FileController implements InitPrint {

    @GetMapping("index")
    public String index(Model model) {
        model.addAttribute("div", new JSONObject().fluentPut("name", "d"));
        model.addAttribute("span", new JSONObject().fluentPut("name", new JSONObject().fluentPut("name", "d")));
        return "index";
    }

}
