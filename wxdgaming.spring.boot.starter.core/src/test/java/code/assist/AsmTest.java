package code.assist;

import org.junit.Test;
import wxdgaming.spring.boot.starter.core.assist.JavassistProxy;
import wxdgaming.spring.boot.starter.core.reflect.MethodUtil;

import java.lang.reflect.Method;

public class AsmTest {


    @Test
    public void a1() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login");
        JavassistProxy javassistProxy = JavassistProxy.of(new LoginHandler(), method);
        javassistProxy.proxyInvoke(new Object[]{1, "123456"});
    }

    @Test
    public void a2() {
        Class<LoginHandler> loginHandlerClass = LoginHandler.class;
        Method method = MethodUtil.findMethod(loginHandlerClass, "login2");
        JavassistProxy javassistProxy = JavassistProxy.of(new LoginHandler(), method);
        javassistProxy.proxyInvoke(new Object[]{true, (byte) 1, 1, 1, "123456"});
    }

}
