package wxdgaming.game.test.script;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.starter.core.SpringReflect;
import wxdgaming.spring.boot.starter.core.SpringUtil;

/**
 * 主容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-20 13:46
 **/
@Component
public class ScriptSpringReflect extends SpringReflect {

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        SpringUtil.scriptApplicationContext = this;
    }

}
