package  wxdgaming.game.message.role;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 更新战斗力 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新战斗力")
public class ResUpdateFightValue extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 55244709;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 当前战斗力 */
    @Tag(1) private long fightValue;


}
