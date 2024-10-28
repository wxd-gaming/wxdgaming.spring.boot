package wxdgaming.spring.boot.webclient;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;

/**
 * ip数据查询
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-28 20:26
 **/
@Getter
@Setter
public class IPInfo extends ObjectBase {

    private String status;
    private String country;
    private String regionName;
    private String city;
    private String query;

}
