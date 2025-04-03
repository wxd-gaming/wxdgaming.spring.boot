package wxdgaming.game.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import wxdgaming.game.test.entity.Account2;
import wxdgaming.game.test.entity.AccountKey;
import wxdgaming.spring.boot.starter.batis.sql.DataJdbcScan;
import wxdgaming.spring.boot.starter.batis.sql.JdbcContext;
import wxdgaming.spring.boot.starter.core.CoreScan;
import wxdgaming.spring.boot.starter.core.SpringReflect;
import wxdgaming.spring.boot.starter.core.SpringUtil;
import wxdgaming.spring.boot.starter.core.collection.MapOf;
import wxdgaming.spring.boot.starter.core.loader.ClassDirLoader;
import wxdgaming.spring.boot.starter.core.loader.JavaCoderCompile;
import wxdgaming.spring.boot.starter.core.threading.ExecutorUtilImpl;
import wxdgaming.spring.boot.starter.net.NetScan;
import wxdgaming.spring.boot.starter.net.SocketSession;
import wxdgaming.spring.boot.starter.net.client.SocketClient;
import wxdgaming.spring.boot.starter.net.httpclient.HttpClientPoolScan;
import wxdgaming.spring.boot.starter.net.module.inner.RpcService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * 启动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-01 17:18
 **/
@Slf4j
@EnableScheduling
@EnableJpaRepositories
@EntityScan("wxdgaming.game.test.entity")
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreScan.class,
                DataJdbcScan.class,
                HttpClientPoolScan.class,
                NetScan.class,
                AppStart.class,
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                MongoAutoConfiguration.class
        }
)
public class AppStart {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext run = SpringApplication.run(AppStart.class, args);
        AppSpringReflect appSpringReflect = run.getBean(AppSpringReflect.class);
        appSpringReflect.getSpringReflectContent().executorInitMethod();
        appSpringReflect.getSpringReflectContent().executorAppStartMethod();

        ClassDirLoader scriptClassLoader;
        Path path = Path.of("script.jar");
        if (Files.exists(path)) {
            scriptClassLoader = new ClassDirLoader(path.toUri().toURL());
        } else {
            new JavaCoderCompile()
                    .parentClassLoader(AppStart.class.getClassLoader())
                    .compilerJava("wxdgaming.game.test-script/src/main/java")
                    .outPutFile("target/scripts", true);
            scriptClassLoader = new ClassDirLoader("target/scripts");
        }

        ConfigurableApplicationContext configurableApplicationContext = SpringUtil.newChild(run, ScriptScan.class, scriptClassLoader);

        SpringReflect scriptSpringReflect = configurableApplicationContext.getBean(SpringReflect.class);
        scriptSpringReflect.getSpringReflectContent().executorInitMethod();

        ExecutorUtilImpl.getInstance().getLogicExecutor().scheduleAtFixedDelay(
                () -> {
                    SocketClient bean = scriptSpringReflect.getBean(SocketClient.class);
                    SocketSession idle = bean.idle();
                    RpcService rpcService = scriptSpringReflect.getBean(RpcService.class);
                    rpcService.request(idle, "test/index", MapOf.newJSONObject().fluentPut("name", 1).toJSONString());
                    rpcService.request(idle, "test/index", MapOf.newJSONObject().fluentPut("name", 2).toJSONString());
                    rpcService.request(idle, "script/index", MapOf.newJSONObject().fluentPut("name", 1).toJSONString());
                    rpcService.request(idle, "script/index", MapOf.newJSONObject().fluentPut("name", 2).toJSONString());
                },
                3,
                3,
                TimeUnit.SECONDS
        );

        ExecutorUtilImpl.getInstance().getLogicExecutor().schedule(
                () -> {
                    JdbcContext jdbcContext = scriptSpringReflect.getBean(JdbcContext.class);
                    {
                        Account2 account2 = new Account2();
                        account2.setUid(new AccountKey(1, "test"));
                        jdbcContext.save(account2);
                    }
                    {
                        Account2 account2 = new Account2();
                        account2.setUid(new AccountKey(2, "test"));
                        jdbcContext.save(account2);
                    }
                },
                3,
                TimeUnit.SECONDS
        );

    }

    @ComponentScan("wxdgaming.game.test.script")
    public static class ScriptScan {

    }

}
