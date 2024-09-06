package wxdgaming.spring.boot.start;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.data.batis.DataBatisScan;
import wxdgaming.spring.boot.data.excel.DataExcelScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.net.NetScan;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.client.TcpSocketClient;
import wxdgaming.spring.boot.net.client.WebSocketClient;
import wxdgaming.spring.boot.rpc.RpcScan;
import wxdgaming.spring.boot.rpc.RpcService;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;
import wxdgaming.spring.boot.web.WebScan;
import wxdgaming.spring.boot.weblua.WebLuaScan;

import java.util.Arrays;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 19:54
 **/
@Slf4j
@EntityScan("wxdgaming.spring.boot.start")
@EnableJpaRepositories("wxdgaming.spring.boot.start")
@SpringBootApplication(
        scanBasePackageClasses = {
                ApplicationStart.class,
                CoreScan.class,
                DataBatisScan.class,
                DataRedisScan.class,
                DataExcelScan.class,
                NetScan.class,
                RpcScan.class,
                WebScan.class,
                WebLuaScan.class,
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                MongoAutoConfiguration.class
        }
)
public class ApplicationStart {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ApplicationStart.class, args);

        SpringUtil ins = SpringUtil.getIns();
        ins.withMethodAnnotated(Start.class)
                .forEach(method -> {
                    try {
                        Object bean = ins.getBean(method.getDeclaringClass());
                        method.setAccessible(true);
                        Object[] array = Arrays.stream(method.getParameterTypes()).map(ins::getBean).toArray();
                        method.invoke(bean, array);
                    } catch (Exception e) {
                        throw new RuntimeException(method.toString(), e);
                    }
                });

        RpcService rpcService = ins.getBean(RpcService.class);

        RpcMessage.ReqRemote rpcMessage = new RpcMessage.ReqRemote();
        rpcMessage
                .setRpcId(1)
                .setPath("rpcTest")
                .setRpcToken(rpcService.getRPC_TOKEN())
                .setParams(new JSONObject().fluentPut("type", 1).toString())
        ;

        try {
            SocketSession session = run.getBean(TcpSocketClient.class).getSession();
            Mono<String> rpc = rpcService.request(session, "rpcTest", new JSONObject().fluentPut("type", 1).toString());
            rpc.subscribe(str -> log.debug("{}", str));
            rpc.block();
        } catch (Exception ignore) {
            log.error("{}", Throw.ofString(ignore, false));
        }

        try {
            SocketSession session = run.getBean(WebSocketClient.class).getSession();
            Mono<String> rpc = rpcService.request(session, "rpcTest", new JSONObject().fluentPut("type", 1).toString());
            rpc.subscribe(str -> log.debug("{}", str));
            rpc.block();
            session.writeAndFlush("string message");
        } catch (Exception ignore) {
            log.error("{}", Throw.ofString(ignore, false));
        }

        // UserRepository userRepository = run.getBean(UserRepository.class);
        //
        // for (int i = 0; i < 100; i++) {
        //     long nanoTime = System.nanoTime();
        //     userRepository.saveAndFlush(new User().setUid(System.nanoTime()).setUserName(RandomStringUtils.randomAlphanumeric(32)));
        //     log.info("插入 耗时：{} ms", (System.nanoTime() - nanoTime) / 10000 / 100f);
        // }
        // {
        //     List<User> users = new ArrayList<>();
        //     for (int i = 0; i < 100; i++) {
        //         users.add(new User().setUid(System.nanoTime()).setUserName(RandomStringUtils.randomAlphanumeric(32)));
        //     }
        //     long nanoTime = System.nanoTime();
        //     userRepository.saveAllAndFlush(users);
        //     log.info("插入 耗时：{} ms", (System.nanoTime() - nanoTime) / 10000 / 100f);
        // }
    }

}
