package wxdgaming.spring.boot.core.io;

/**
 * 资源读取
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-14 16:22
 **/
public class ResourcesUtil {


    public void t0(String[] args) {
        FileUtil.resourceStreams(".").forEach(record -> System.out.println(record.t1()));

        FileUtil.resourceStreams("META-INF/LICENSE").forEach(record -> System.out.println(record.t1()));
        FileUtil.resourceStreams("META-INF/LICENSE").forEach(record -> System.out.println(record.t1()));
    }

}
