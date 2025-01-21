package code;

import code.pgsql.PgsqlLogTest;
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
@ActiveProfiles("pgsql")
@SpringBootApplication
@EntityScan(basePackages = {"code.pgsql"})
@EnableJpaRepositories("code.pgsql")
@SpringBootTest(classes = {CoreScan.class, DataJdbcScan.class})
public class SpringPgsqlTest {

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
                    List<PgsqlLogTest> logTests = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        PgsqlLogTest logTest = new PgsqlLogTest()
                                .setUid(hexId.newId())
                                .setLogType(RandomUtils.randomItem(strings))
                                .setName(String.valueOf(i));
                        logTest.getSensors().put("a", RandomUtils.random(1, 10000));
                        logTest.getSensors().put("b", RandomUtils.random(1, 10000));
                        logTest.getSensors().put("c", RandomUtils.random(1, 10000));
                        logTest.getSensors().put("d", RandomUtils.random(1, 10000));
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
        long count = jdbcContext.count(PgsqlLogTest.class);
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select count=" + count);
    }

    @Test
    @RepeatedTest(5)
    public void selectJsonA() {
        selectJson("a");
    }

    public void selectJson(String field) {
        long nanoTime = System.nanoTime();
        String string = String.valueOf(RandomUtils.random(1, 10000));
        List<PgsqlLogTest> all2Stream = jdbcContext.findAll(
                "from " + PgsqlLogTest.class.getSimpleName() + " where jsonb_extract_path_text(sensors, ?1) = ?2",
                PgsqlLogTest.class,
                field,
                string
        );
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select $" + field + "=" + string + " - count = " + all2Stream.size());
        all2Stream.forEach(System.out::println);
    }

    @Test
    @RepeatedTest(5)
    public void selectJson2() {
        long nanoTime = System.nanoTime();
        String string = String.valueOf(RandomUtils.random(1, 10000));
        List<PgsqlLogTest> all2Stream = jdbcContext.findAll(
                "from " + PgsqlLogTest.class.getSimpleName() + " where jsonb_extract_path_text(sensors,'e','aa') = ?1",
                PgsqlLogTest.class,
                string
        );
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select $.e.aa=" + string + " - count = " + all2Stream.size());
        all2Stream.forEach(System.out::println);
    }


}
