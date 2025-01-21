package code;

import code.mysql.MysqlLogTest;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.core.lang.RandomUtils;
import wxdgaming.spring.boot.data.batis.DataJdbcScan;
import wxdgaming.spring.boot.data.batis.JdbcContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 通过spring注入测试jdbc
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-31 09:40
 **/
@Slf4j
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("mysql")
@SpringBootApplication
@EntityScan(basePackages = {"code.mysql"})
@EnableJpaRepositories("code.mysql")
@SpringBootTest(classes = {CoreScan.class, DataJdbcScan.class})
public class SpringMysqlTest {

    static HexId hexId = new HexId(1);
    @Autowired JdbcContext jdbcContext;

    @Test
    public void insert_10w() {
        insert(100);
    }

    @Test
    public void insert_1w() {
        insert(10);
    }

    public void insert(int count) {
        List<String> strings = List.of("item-log", "login-log", "pay-log");
        IntStream.range(0, count)
                .parallel()
                .forEach(k -> {
                    long nanoTime = System.nanoTime();
                    List<MysqlLogTest> logTests = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        MysqlLogTest logTest = new MysqlLogTest()
                                .setUid(hexId.newId())
                                .setLogType(RandomUtils.randomItem(strings))
                                .setName(String.valueOf(i));
                        logTest.getSensors().put("a", String.valueOf(RandomUtils.random(1, 10000)));
                        logTest.getSensors().put("b", String.valueOf(RandomUtils.random(1, 10000)));
                        logTest.getSensors().put("c", String.valueOf(RandomUtils.random(1, 10000)));
                        logTest.getSensors().put("d", String.valueOf(RandomUtils.random(1, 10000)));
                        logTest.getSensors().put("e", new JSONObject().fluentPut("aa", String.valueOf(RandomUtils.random(1, 10000))));
                        logTests.add(logTest);
                    }
                    jdbcContext.batchInsert(logTests);
                    System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
                });
    }

    @Test
    @RepeatedTest(5)
    public void selectCount() {
        long nanoTime = System.nanoTime();
        long count = jdbcContext.count(MysqlLogTest.class);
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select count=" + count);
    }

    @Test
    @RepeatedTest(5)
    public void selectJsonA() {
        selectJson("a");
    }

    @Test
    @RepeatedTest(5)
    public void selectJsonEA() {
        selectJson("e.aa");
    }

    public void selectJson(String json_path) {
        long nanoTime = System.nanoTime();
        String string = String.valueOf(RandomUtils.random(1, 10000));
        List<MysqlLogTest> all2Stream = jdbcContext.findAll(
                "from " + MysqlLogTest.class.getSimpleName() + " where json_extract(sensors,?1) = ?2",
                MysqlLogTest.class,
                "$." + json_path,
                string
        );
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select $sensors." + json_path + "=" + string + " - count = " + all2Stream.size());
        all2Stream.forEach(System.out::println);
    }


}
