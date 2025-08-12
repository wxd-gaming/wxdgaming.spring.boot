package wxdgaming.game.login.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.batis.Entity;
import wxdgaming.spring.boot.batis.ann.DbColumn;
import wxdgaming.spring.boot.batis.ann.DbTable;

/**
 * 登录数据
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:29
 **/
@Getter
@Setter
@DbTable
public class UserData extends Entity {

    @DbColumn(key = true, length = 64)
    private String account;
    /** 登录密钥，如果是渠道sdk这个其实无意义 */
    @DbColumn(length = 128)
    private String token;
    private long createTime;
    @DbColumn(index = true)
    private int appId;
    @DbColumn(index = true, length = 64)
    private String platform;
    /** 平台返回的 Channel id */
    @DbColumn(index = true, length = 64)
    private String platformChannelId;
    /** 平台返回的userid */
    @DbColumn(index = true, length = 64)
    private String platformUserId;


}
