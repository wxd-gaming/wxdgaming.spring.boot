package wxdgaming.spring.boot.core.system;


import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-04-02 15:07
 **/
public class JvmUtil {

    public static String processIDString = null;
    public static Integer processIDInt = null;

    public static void init() {

        setProperty("sun.stdout.encoding", "utf-8");
        setProperty("sun.stderr.encoding", "utf-8");
        setProperty("sun.jnu.encoding", "utf-8");
        setProperty("file.encoding", "utf-8");
        /** ssl bug */
        setProperty("javax.net.ssl.sessionCacheSize", "2");

        initProcessID();


        // 虚拟线程池的默认值
        int parallelism, maxPoolSize, minRunnable;
        String parallelismValue = System.getProperty("jdk.virtualThreadScheduler.parallelism");
        String maxPoolSizeValue = System.getProperty("jdk.virtualThreadScheduler.maxPoolSize");
        String minRunnableValue = System.getProperty("jdk.virtualThreadScheduler.minRunnable");

    }

    public static <R> R getProperty(String key, R defaultValue, Function<String, R> convert) {
        String property = System.getProperty(key);
        if (property != null) {
            if (convert != null) {
                return convert.apply(property);
            } else {
                return (R) property;
            }
        }
        return defaultValue;
    }

    /**
     * 设置配置
     */
    public static String setProperty(String key, Object value) {
        return System.setProperty(key, String.valueOf(value));
    }

    /**
     * 获取当前进程的进程号
     */
    private static void initProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        processIDString = runtimeMXBean.getName().split("@")[0];
        processIDInt = Integer.parseInt(processIDString);
    }

    /** 获取程序启动的进程号 */
    public static int processID() {
        if (processIDInt == null) {
            initProcessID();
        }
        return processIDInt;
    }

    /** 获取程序启动的进程号 */
    public static String processIDString() {
        if (processIDString == null) {
            initProcessID();
        }
        return processIDString;
    }


    /**
     * 强制结束进程
     * <p> {@link Runtime#getRuntime()#halt(int)}
     * <p>调用这个方法不会触发jvm退出消息钩子
     *
     * @param status 状态码
     */
    public static void halt(int status) {
        for (int kk = 3; kk >= 1; kk--) {
            System.out.println("进程退出倒计时：" + kk + " 秒");
            try {
                Thread.sleep(1000);
            } catch (Exception ignore) {}
        }
        System.out.println("进程退出：" + status);
        Runtime.getRuntime().halt(status);
    }

    /** 添加jvm退出信号消息钩子 */
    public static void addShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

    /** 清理配置 */
    public static String clearProperty(String key) {
        return System.clearProperty(key);
    }

    /** 查看所有的系统配置 */
    public static void showProperties() {
        System.out.println(properties());
    }

    /**
     * 查看所有的系统配置
     */
    public static String properties() {
        StringBuilder stringBuilder = new StringBuilder();
        TreeMap<Object, Object> treeMap = new TreeMap<>(System.getProperties());
        for (Map.Entry<Object, Object> entry : treeMap.entrySet()) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(entry.getKey()).append(" = ").append(entry.getValue());
        }
        return stringBuilder.toString();
    }

    /**
     * 采用 GMT时区设置
     */
    public static TimeZone setTimeZone(String zoneId) {
        if (zoneId == null || zoneId.isBlank()) {
            throw new RuntimeException("zoneId = " + zoneId);
        }
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        TimeZone.setDefault(timeZone);
        System.setProperty("user.timezone", timeZone.getID());
        System.out.println("user.timezone = " + timeZone());
        return timeZone;
    }

    /** 当前程序运行时区 */
    public final static String timeZone() {
        return System.getProperty("user.timezone");
    }

    /** 判断是不是linux系统 */
    public static boolean isLinuxOs() {
        return !osName().toLowerCase().startsWith("win");
    }

    public static String osName() {
        return System.getProperty("os.name");
    }

    public static String osVersion() {
        return System.getProperty("os.version");
    }

    /** 用户目录，启动目录 */
    public static String userHome() {
        return System.getProperty("user.dir");
    }

    public static String jdkHome() {
        return System.getProperty("java.ext.dirs");
    }

    public static String jreHome() {
        return System.getProperty("java.home");
    }

    public static String javaClassPath() {
        return System.getProperty("java.class.path");
    }

    /**
     * ssl Session 缓存数量
     */
    public static String sslSessionCacheSize() {
        return System.getProperty("javax.net.ssl.sessionCacheSize");
    }
}
