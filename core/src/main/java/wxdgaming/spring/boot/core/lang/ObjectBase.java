package wxdgaming.spring.boot.core.lang;


import org.openjdk.jol.info.GraphLayout;
import wxdgaming.spring.boot.core.format.data.Data2Json;

/**
 * 实现一些序列号接口，重写 hashcode
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-09-12 15:39
 **/
public abstract class ObjectBase
        implements Data2Json {

    public ObjectBase() {
    }

    /** 内存大小,很消耗内存，谨慎使用 */
    public long totalSize() {
        return GraphLayout.parseInstance(this).totalSize();
    }

    @Override
    public String toString() {
        return toJson();
    }

}
