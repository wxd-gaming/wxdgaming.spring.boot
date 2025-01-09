package wxdgaming.spring.boot.net.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ssl.SslContextByJks;
import wxdgaming.spring.boot.core.ssl.SslContextNoFile;
import wxdgaming.spring.boot.core.ssl.SslProtocolType;
import wxdgaming.spring.boot.core.util.StringsUtil;

import javax.net.ssl.SSLContext;

@Getter
@Setter
@Accessors(chain = true)
public class ClientConfig {

    protected String host = "127.0.0.1";
    private int port = 18001;
    /** 帧最大字节数 */
    private int maxFrameBytes = 8 * 1024 * 1024;
    /** 每秒钟帧的最大数量 */
    private int maxFrameLength = -1;
    private int idleTimeout = 30;
    private int connectTimeout = 2000;
    /** 默认最大连接数1 */
    private int maxConnectNum = 1;
    /** 断线重连 */
    private boolean enableReconnection = false;
    private boolean enableSsl = false;
    private boolean enableRpc = true;
    private boolean useWebSocket = false;
    /** 扫描处理器包名 */
    private String[] scanHandlers = new String[0];
    /** 扫描消息的包名 */
    private String[] scanMessages = new String[0];
    private String prefix = "/wxd-gaming";
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
