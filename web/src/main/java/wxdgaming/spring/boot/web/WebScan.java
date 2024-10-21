package wxdgaming.spring.boot.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import wxdgaming.spring.boot.core.CoreScan;

@EnableWebMvc
@ComponentScan(basePackageClasses = {CoreScan.class})
@ComponentScan()
public class WebScan {

}
