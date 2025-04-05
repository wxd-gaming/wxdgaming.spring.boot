package wxdgaming.game.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-05 18:12
 **/
@Component
public class ATest {

    private final ApplicationContext application;

    @Autowired
    public ATest(ApplicationContext application) {
        this.application = application;
    }

    public void test() {
        AppSpringReflect appSpringReflect = new AppSpringReflect();
        System.out.println(1);
    }

}
