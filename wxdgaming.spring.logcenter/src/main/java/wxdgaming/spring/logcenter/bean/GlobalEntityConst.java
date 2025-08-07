package wxdgaming.spring.logcenter.bean;

import lombok.Getter;
import wxdgaming.spring.boot.batis.sql.SqlDataHelper;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.logcenter.entity.GlobalEntity;

import java.util.Map;

/**
 * 全局配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 16:49
 **/
@Getter
public enum GlobalEntityConst {
    None(0, "默认值") {
        @Override public GlobalEntity queryEntity(SqlDataHelper sqlDataHelper) {
            throw new UnsupportedOperationException("不支持此操作");
        }
    },
    LogTable(1, "日志表"),
    ;

    private static final Map<Integer, GlobalEntityConst> static_map = MapOf.ofMap(GlobalEntityConst::getCode, GlobalEntityConst.values());

    public static GlobalEntityConst of(int value) {
        return static_map.get(value);
    }

    public static GlobalEntityConst ofOrException(int value) {
        GlobalEntityConst tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    GlobalEntityConst(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public GlobalEntity queryEntity(SqlDataHelper sqlDataHelper) {
        GlobalEntity byKey = sqlDataHelper.findByKey(GlobalEntity.class, this.getCode());
        if (byKey == null) {
            byKey = new GlobalEntity();
            byKey.setUid(this.getCode());
            byKey.setNewEntity(true);
            sqlDataHelper.save(byKey);
        }
        return byKey;
    }

}