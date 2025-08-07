package wxdgaming.spring.boot.net.httpclient;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.lang.ObjectBase;

import java.util.function.Supplier;

/**
 * 配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 10:26
 **/
@Getter
@Setter
@Accessors(chain = true)
public class HttpClientConfig extends ObjectBase {

    public static Supplier<HttpClientConfig> DEFAULT = () -> new HttpClientConfig()
            .setRouteMaxSize(500)
            .setTotalMaxSize(5000)
            .setResetTimeM(30)
            .setConnectionRequestTimeout(3000)
            .setConnectTimeOut(3000)
            .setKeepAliveTimeout(3000)
            .setReadTimeout(3000)
            .setSslProtocol("TLS")
            .setAutoUseGzip(false);

    /** 每个路由创建的最大连接数 */
    @JSONField(ordinal = 1)
    private int routeMaxSize;
    /** 总链接数 */
    @JSONField(ordinal = 2)
    private int totalMaxSize;
    @JSONField(ordinal = 3)
    private int resetTimeM;
    @JSONField(ordinal = 4)
    private int connectionRequestTimeout;
    @JSONField(ordinal = 5)
    private int connectTimeOut;
    @JSONField(ordinal = 6)
    private int readTimeout;
    @JSONField(ordinal = 7)
    private int keepAliveTimeout;
    @JSONField(ordinal = 8)
    private String sslProtocol;
    @JSONField(ordinal = 9)
    private boolean autoUseGzip = false;

}
