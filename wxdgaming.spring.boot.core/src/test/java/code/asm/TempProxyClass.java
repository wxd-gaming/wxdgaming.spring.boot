package code.asm;

public class TempProxyClass extends wxdgaming.spring.boot.core.assist.Javassist2Proxy {

    public Object proxy0(Object[] args) {
        SimpleClass simpleClass = (SimpleClass) instance;
        Object result = null;
        simpleClass.simpleMethod();
        return result;
    }
}
