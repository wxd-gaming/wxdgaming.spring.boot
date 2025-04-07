package wxdgaming.spring.boot.starter.core.assist;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * assist asm 的代理类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-03 17:26
 **/
@Slf4j
@Getter
public class JavassistInvoke {

    /** 创建代理对象 */
    public static JavassistInvoke of(Object invokeInstance, Method method) {
        Class<?> invokeClass = invokeInstance.getClass();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilderArgs = new StringBuilder();
        stringBuilder.append("public Object invoke(Object[] args) {\n");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];

            if (!stringBuilderArgs.isEmpty()) stringBuilderArgs.append(",\n");

            if (parameterType == int.class) {
                stringBuilderArgs.append("((Integer)args[").append(i).append("]).intValue()");
            } else if (parameterType == long.class) {
                stringBuilderArgs.append("((Long)args[").append(i).append("]).longValue()");
            } else if (parameterType == double.class) {
                stringBuilderArgs.append("((Double)args[").append(i).append("]).doubleValue()");
            } else if (parameterType == float.class) {
                stringBuilderArgs.append("((Float)args[").append(i).append("]).floatValue()");
            } else if (parameterType == short.class) {
                stringBuilderArgs.append("((Short)args[").append(i).append("]).shortValue()");
            } else if (parameterType == byte.class) {
                stringBuilderArgs.append("((Byte)args[").append(i).append("]).byteValue()");
            } else if (parameterType == boolean.class) {
                stringBuilderArgs.append("((Boolean)args[").append(i).append("]).booleanValue()");
            } else {
                stringBuilderArgs.append("(" + parameterType.getName() + ")args[").append(i).append("]");
            }
        }
        stringBuilder.append("    ").append(invokeClass.getName()).append(" proxy = ").append("(").append(invokeClass.getName()).append(")instance;").append("\n");
        stringBuilder.append("    ").append("Object result = ").append("null").append(";").append("\n");
        if (method.getReturnType() != void.class) {
            stringBuilder.append("    ").append("result = proxy.").append(method.getName()).append("(").append(stringBuilderArgs).append(");").append("\n");
        } else {
            stringBuilder.append("    ").append("proxy.").append(method.getName()).append("(\n").append(stringBuilderArgs).append("\n);").append("\n");
        }
        stringBuilder.append("    ").append("return result;").append("\n");
        stringBuilder.append("}");
        String methodBody = stringBuilder.toString();
        if (log.isDebugEnabled()) {
            log.debug("\n{}", methodBody);
        }
        JavaAssistBox.JavaAssist javaAssist = JavaAssistBox.DefaultJavaAssistBox.extendSuperclass(JavassistInvoke.class, invokeClass.getClassLoader());
        javaAssist.createMethod(methodBody);
        if (log.isDebugEnabled()) {
            javaAssist.writeFile("target/bin");
        }
        // try {
        //     ClassDirLoader classDirLoader = new ClassDirLoader("target/bin");
        //     Class<?> aClass = classDirLoader.loadClass(javaAssist.getCtClass().getName());
        //     JavassistInvoke newInstance = (JavassistInvoke) aClass.getDeclaredConstructor().newInstance();
        //     newInstance.init(invokeInstance, method);
        //     return newInstance;
        // } catch (Exception e) {
        //     throw new RuntimeException(e);
        // }
        JavassistInvoke javassistInvoke = javaAssist.toInstance();
        javassistInvoke.init(invokeInstance, method);
        return javassistInvoke;
    }


    protected Object instance;
    protected Method method;

    public void init(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object invoke(Object[] args) {
        throw new RuntimeException("not implement");
    }

}
