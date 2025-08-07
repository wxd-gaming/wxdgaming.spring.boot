package wxdgaming.spring.logcenter.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志映射信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 19:46
 **/
@Getter
@Setter
public class LogMappingInfo extends ObjectBase {

    private String logName;
    /** 表注释 */
    private String logComment;
    /** 是否开启分区 是按照每天进行区分 */
    private boolean partition;
    private List<LogField> fieldList = new ArrayList<>();

}
