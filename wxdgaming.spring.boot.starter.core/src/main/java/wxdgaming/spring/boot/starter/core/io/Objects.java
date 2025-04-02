package wxdgaming.spring.boot.starter.core.io;


import wxdgaming.spring.boot.starter.core.Throw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * 数据对象合并
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-07-29 10:33
 */
public class Objects {

    public static boolean compare(Object o1, Object o2) {
        long hashCode1 = getHashCode(o1);
        long hashCode2 = getHashCode(o2);

        return hashCode1 == hashCode2;
    }

    public static long getHashCode(Object object) {
        long hashcode = 0;
        try {
            Class clazz = object.getClass();// 根据类名获得其对应的Class对象 写上你想要的类名就是了 注意是全名 如果有包的话要加上 比如java.Lang.String
            Field[] fields = clazz.getDeclaredFields();// 根据Class对象获得属性 私有的也可以获得
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                    || Modifier.isTransient(field.getModifiers())
                    || Modifier.isFinal(field.getModifiers())) {
                    //                    System.out.println(" 类：" + clazz.getName() + " 字段：" + field.getName() + " is transient or static or final;");
                    continue;
                }
                Object get = field.get(object);
                if (get != null) {
                    hashcode += java.util.Objects.hashCode(get);
                }
                //                System.out.println(field.getName() + " " + field.getType().getName() + "," + get);//打印每个属性的类型名字
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return hashcode;
    }

    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(obj);
                oos.flush();
                return baos.toByteArray();
            }
        } catch (Throwable ex) {
            throw Throw.of("对象转 byte[] 出现错误", ex);
        }
    }

    /**
     * @param buf
     * @return
     */
    public static <R> R toObject(byte[] buf) {
        if (buf == null || buf.length < 1) {
            return null;
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buf)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return (R) ois.readObject();
            }
        } catch (Throwable ex) {
            throw Throw.of("byte[] 转 对象 出现错误", ex);
        }
    }

    /**
     * 深拷贝 效率,高于Fastjson以及BeanUtils
     *
     * @param <R>
     * @param obj
     * @return
     */
    public static <R> R deepCopy(Object obj) {
        return (R) toObject(toBytes(obj));
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /** 检查数组是否包含某个值 */
    public static boolean checkHave(int[] ints, int value) {
        for (int i : ints) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    /** 检查数组是否包含某个值 */
    public static boolean checkHave(long[] longs, long value) {
        for (long i : longs) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean nonNull(Object obj) {
        return obj != null;
    }

    public static boolean nullEmpty(Object obj) {
        return switch (obj) {
            case null -> true;
            case String string -> string.isBlank();
            case Collection<?> collection -> collection.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };
    }

    public static boolean nonNullEmpty(Object obj) {
        return !nullEmpty(obj);
    }

    /** 返回非null的那个参数，优先第一个 */
    public static <R> R returnNonNull(R... args) {
        for (int i = 0; i < args.length; i++) {
            R arg = args[i];
            if (arg != null) {
                return arg;
            }
        }
        throw new NullPointerException();
    }

}
