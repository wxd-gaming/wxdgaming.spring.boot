package code;

import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import org.junit.Test;
import wxdgaming.spring.boot.assist.JavaAssistBox;
import wxdgaming.spring.boot.core.function.STVFunction3;
import wxdgaming.spring.boot.core.system.LambdaUtil;
import wxdgaming.spring.boot.core.system.MethodUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.message.PojoBase;
import wxdgaming.spring.boot.net.message.inner.InnerMessage;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 代理测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-21 20:27
 */
public class ProxyTest {

    int fori = 100_0000;
    Class<? extends ProxyTest> aClass;
    Set<Method> methods;

    public ProxyTest() {
        aClass = this.getClass();
        methods = MethodUtil.allMethods(aClass);
    }

    @Test
    public void t0() throws Exception {
        long l = System.nanoTime();
        for (int i = 0; i < fori; i++) {
            for (Method method : MethodUtil.allMethods(aClass)) {
                MP annotation = method.getAnnotation(MP.class);
                if (annotation == null) continue;
                method.invoke(this, null, null);
            }
        }
        System.out.println("耗时：" + ((System.nanoTime() - l) / 10000 / 100f) + " ms");
    }

    @Test
    public void t1() throws Exception {
        long l = System.nanoTime();
        for (int i = 0; i < fori; i++) {
            for (Method method : methods) {
                MP annotation = method.getAnnotation(MP.class);
                if (annotation == null) continue;
                method.invoke(this, null, null);
            }
        }
        System.out.println("耗时：" + ((System.nanoTime() - l) / 10000 / 100f) + " ms");
    }

    @Test
    public void t2() throws Exception {
        long l = System.nanoTime();
        for (Method method : methods) {
            MP annotation = method.getAnnotation(MP.class);
            if (annotation == null) continue;
            method.setAccessible(true);
            method.invoke(this, null, null);
        }
        System.out.println("耗时：" + ((System.nanoTime() - l) / 10000 / 100f) + " ms");
    }

    @Test
    public void t3() throws NotFoundException {
        List<AsmMethodProxy> proxies = new ArrayList<>();
        for (Method method : methods) {
            MP annotation = method.getAnnotation(MP.class);
            if (annotation == null) continue;
            JavaAssistBox.JavaAssist javaAssist = JavaAssistBox.DefaultJavaAssistBox.implInterfaces(
                    AsmMethodProxy.class.getName(),
                    aClass.getClassLoader(),
                    AsmMethodProxy.class);

            javaAssist.importPackage(AsmMethodProxy.class, SocketSession.class, PojoBase.class, ProxyTest.class);

            javaAssist.createMethod(
                    Modifier.PUBLIC,
                    CtClass.voidType,
                    "proxy",
                    new Class[]{Object.class, SocketSession.class, PojoBase.class},
                    ctMethod -> {

                        Class<?>[] parameterTypes = method.getParameterTypes();
                        List<String> list = Arrays.stream(parameterTypes).map(Class::getName).collect(Collectors.toList());
                        list.add(0, aClass.getName());
                        list.add(1, method.getName());
                        ctMethod.setBody("""
                                {
                                ((%s)$1).%s((%s)$2,(%s)$3);
                                }
                                """
                                .formatted(list.toArray())
                        );

                    }
            );
            javaAssist.writeFile("target/out");
            AsmMethodProxy instance = javaAssist.toInstance();
            proxies.add(instance);

        }
        long l = System.nanoTime();
        for (AsmMethodProxy instance : proxies) {
            instance.proxy(this, null, null);
        }
        System.out.println("耗时：" + ((System.nanoTime() - l) / 10000 / 100f) + " ms");
    }

    @Test
    public void t4() throws Exception {
        STVFunction3<Object, MethodProxy, SocketSession, InnerMessage.ReqHeart> proxy = MethodProxy::proxy;
        List<MethodProxy> methodProxies = new ArrayList<>();
        LambdaUtil.findDelegate(MethodProxy.class, proxy.ofMethod(), this, methodProxyMapping -> {
            methodProxies.add(methodProxyMapping.getMapping());
        });
        long l = System.nanoTime();
        for (MethodProxy instance : methodProxies) {
            instance.proxy(null, null);
        }
        System.out.println("耗时：" + ((System.nanoTime() - l) / 10000 / 100f) + " ms");
    }

    @MP
    public void m1(SocketSession session, InnerMessage.ReqHeart reqHeart) {
        // System.out.println("m1");
    }

    @MP
    public void m2(SocketSession session, InnerMessage.ResHeart resHeart) {
        // System.out.println("m2");
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MP {}


    public interface AsmMethodProxy {

        void proxy(Object instance, SocketSession session, PojoBase pojoBase);

    }

    public interface MethodProxy {

        void proxy(SocketSession session, InnerMessage.ReqHeart pojoBase);

    }

}
