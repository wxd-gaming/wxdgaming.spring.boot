package wxdgaming.spring.boot.net.ssl;


import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.io.FileUtil;
import wxdgaming.spring.boot.core.lang.Tuple2;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * https协议证书
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-12-18 16:18
 **/
@Slf4j
public class SslContextByJks implements Serializable {

    private static final ConcurrentHashMap<SslProtocolType, ConcurrentHashMap<String, SSLContext>> sslContextMap = new ConcurrentHashMap<>();

    public static SSLContext sslContext(SslProtocolType sslProtocolType, String jks_path, String jks_pwd_path) {
        Map<String, SSLContext> row = sslContextMap.computeIfAbsent(sslProtocolType, l -> new ConcurrentHashMap<>());
        return row.computeIfAbsent(jks_path, l -> {
            try {
                AtomicReference<InputStream> streams = new AtomicReference<>();
                AtomicReference<String> pwd = new AtomicReference<>();
                // 获取当前运行的JAR文件
                Tuple2<Path, byte[]> jksStream = FileUtil.findInputStream(SslContextByJks.class.getClassLoader(), jks_path);
                ByteArrayInputStream newValue = new ByteArrayInputStream(jksStream.getRight());
                streams.set(newValue);
                System.out.printf("读取文件目录：jks=%s, 文件大小：%s\n", jksStream.getLeft(), jksStream.getRight().length);
                pwd.set(jks_pwd_path);

                Tuple2<Path, byte[]> pwdStream = FileUtil.findInputStream(SslContextByJks.class.getClassLoader(), jks_pwd_path);
                if (pwdStream != null) {
                    // 判断是否为资源文件
                    System.out.printf("读取文件目录：jkspwd=%s\n", pwdStream.getLeft());
                    pwd.set(new String(pwdStream.getRight(), StandardCharsets.UTF_8));
                }
                return initSSLContext(sslProtocolType, "jks", streams.get(), pwd.get(), pwd.get());
            } catch (Exception e) {
                throw new RuntimeException("读取jks文件异常", e);
            }
        });
    }

    public static SSLContext initSSLContext(SslProtocolType sslProtocolType,
                                            String keyStoreType,
                                            InputStream keyStoreStream,
                                            String certificatePassword,
                                            String keystorePassword) throws Exception {
        // 密钥管理器
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        // 密钥库KeyStore
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        // 加载服务端的KeyStore  ；sNetty是生成仓库时设置的密码，用于检查密钥库完整性的密码
        ks.load(keyStoreStream, certificatePassword.toCharArray());
        // 初始化密钥管理器
        kmf.init(ks, keystorePassword.toCharArray());
        tmf.init(ks);
        // 获取安全套接字协议（TLS协议）的对象
        SSLContext sslContext = SSLContext.getInstance(sslProtocolType.getTypeName());
        // 初始化此上下文
        // 参数一：认证的密钥      参数二：对等信任认证  参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        sslContext.getServerSessionContext().setSessionCacheSize(2);
        return sslContext;
    }

}
