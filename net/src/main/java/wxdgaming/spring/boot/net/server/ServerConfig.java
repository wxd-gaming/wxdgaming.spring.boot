package wxdgaming.spring.boot.net.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ssl.SslContextByJks;
import wxdgaming.spring.boot.core.ssl.SslContextNoFile;
import wxdgaming.spring.boot.core.ssl.SslProtocolType;
import wxdgaming.spring.boot.core.util.StringsUtil;

import javax.net.ssl.SSLContext;

/** 配置 */
@Getter
@Setter
@Accessors(chain = true)
public class ServerConfig {

    private String serviceClass;
    private int port = 18001;
    /** 帧最大字节数 */
    private int maxFrameBytes = 8 * 1024 * 1024;
    /** 每秒钟帧的最大数量 */
    private int maxFrameLength = -1;
    private int idleTimeout = 30;
    /** 是否开启 ssl */
    private boolean enableSsl = false;
    /** 是否开启 web socket */
    private boolean enableWebSocket = false;
    /** 扫描的包名 */
    private String[] scanPkgs = new String[0];
    private String webSocketPrefix = "/wxd-gaming";
    /** 默认的 ssl 类型 */
    private SslProtocolType sslProtocolType = SslProtocolType.TLSV12;
    /** jks 路径 */
    private String jks_path = "";
    /** jks 密钥 */
    private String jks_pwd_path = "";

    private SSLContext sslContext = null;

    public SSLContext getSslContext() {
        if (sslContext == null) {
            if (StringsUtil.notEmptyOrNull(jks_path)) {
                sslContext = SslContextByJks.sslContext(sslProtocolType, jks_path, jks_pwd_path);
            } else {
                sslContext = SslContextNoFile.sslContext(sslProtocolType);
            }
        }
        return sslContext;
    }
}
