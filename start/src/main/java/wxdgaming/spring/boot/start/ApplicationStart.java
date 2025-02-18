package wxdgaming.spring.boot.start;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.broker.BrokerScan;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.data.batis.DataJdbcScan;
import wxdgaming.spring.boot.data.excel.DataExcelScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.net.NetScan;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.client.TcpSocketClient;
import wxdgaming.spring.boot.rpc.RpcDispatcher;
import wxdgaming.spring.boot.rpc.RpcScan;
import wxdgaming.spring.boot.rpc.RpcService;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;
import wxdgaming.spring.boot.start.bean.entity.User;
import wxdgaming.spring.boot.start.bean.repository.UserRepository;
import wxdgaming.spring.boot.web.WebScan;
import wxdgaming.spring.boot.weblua.WebLuaScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                DataJdbcScan.class,
                DataRedisScan.class,
                DataExcelScan.class,
                NetScan.class,
                BrokerScan.class,
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
        AppSpringReflect runBean = run.getBean(AppSpringReflect.class);
        runBean.content().executorAppStartMethod();

        RedisTemplate<String, Object> redisTemplate = runBean.getBean(RedisTemplate.class);
        redisTemplate.opsForValue().setIfAbsent("1", "1");
        RedisTemplate<String, Object> redisTemplate2 = runBean.getBean("redisTemplate2");
        redisTemplate2.opsForValue().set("2", "2");
        long l = System.nanoTime();
        HashMap<String, String> putAll = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            // redisTemplate2.opsForValue().set(String.valueOf(i), String.valueOf(i));
            putAll.put(String.valueOf(i), String.valueOf(i));
        }
        redisTemplate2.opsForHash().putAll("redisTemplate2", putAll);
        log.info("redis 耗时：{} ms", (System.nanoTime() - l) / 10000 / 100f);
        RpcService rpcService = runBean.getBean(RpcService.class);
        RpcDispatcher rpcDispatcher = rpcService.getRpcDispatcher();

        RpcMessage.ReqRPC rpcMessage = new RpcMessage.ReqRPC();
        rpcMessage
                .setRpcId(1)
                .setPath("rpcTest")
                .setParams(new JSONObject().fluentPut("type", 1).toString())
        ;

        try {
            SocketSession session = run.getBean(TcpSocketClient.class).idleSession();
            Mono<String> rpc = rpcDispatcher.request(session, 0, "rpcTest", new JSONObject().fluentPut("type", 1).toString());
            rpc.subscribe(str -> log.debug("{}", str));
            rpc.block();
        } catch (Exception e) {
            log.error("{}", Throw.ofString(e, false));
        }

        UserRepository userRepository = run.getBean(UserRepository.class);

        for (int i = 0; i < 1; i++) {
            long nanoTime = System.nanoTime();
            userRepository.saveAndFlush(new User().setUid(System.nanoTime()).setUserName(RandomStringUtils.randomAlphanumeric(32)));
            log.info("插入 耗时：{} ms", (System.nanoTime() - nanoTime) / 10000 / 100f);
        }
        {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                users.add(new User().setUid(System.nanoTime()).setUserName(RandomStringUtils.randomAlphanumeric(32)));
            }
            long nanoTime = System.nanoTime();
            userRepository.saveAllAndFlush(users);
            log.info("插入 耗时：{} ms", (System.nanoTime() - nanoTime) / 10000 / 100f);
        }
    }

}
