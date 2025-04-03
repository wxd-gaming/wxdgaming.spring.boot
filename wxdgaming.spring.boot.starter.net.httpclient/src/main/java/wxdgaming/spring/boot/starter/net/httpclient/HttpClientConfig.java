package wxdgaming.spring.boot.starter.net.httpclient;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.starter.core.lang.ObjectBase;

/**
 * 配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 10:26
 **/
@Getter
@Setter
public class HttpClientConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private int core;
    @JSONField(ordinal = 2)
    private int max;
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
    private boolean autoUseGzip;

    public HttpClientConfig() {
    }

    @JSONCreator
    public HttpClientConfig(int core,
                            int max,
                            int resetTimeM,
                            int connectionRequestTimeout,
                            int connectTimeOut,
                            int readTimeout,
                            int keepAliveTimeout,
                            String sslProtocol,
                            boolean autoUseGzip) {
        this.core = core;
        this.max = max;
        this.resetTimeM = resetTimeM;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.connectTimeOut = connectTimeOut;
        this.readTimeout = readTimeout;
        this.keepAliveTimeout = keepAliveTimeout;
        this.sslProtocol = sslProtocol;
        this.autoUseGzip = autoUseGzip;
    }
}
