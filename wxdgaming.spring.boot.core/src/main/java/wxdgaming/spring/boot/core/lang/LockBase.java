package wxdgaming.spring.boot.core.lang;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-06-26 17:12
 **/
@Getter
public class LockBase implements ILock {

    @JSONField(serialize = false, deserialize = false)
    protected transient final ReentrantLock lock = new ReentrantLock();

}
