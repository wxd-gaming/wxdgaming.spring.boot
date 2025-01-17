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
import java.util.stream.Stream;

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
    public void insert() {
        IntStream.range(0, 100)
                .parallel()
                .forEach(k -> {
                    long nanoTime = System.nanoTime();
                    List<MysqlLogTest> logTests = new ArrayList<>();
                    for (int i = 0; i < 1000; i++) {
                        MysqlLogTest logTest = new MysqlLogTest().setUid(hexId.newId())
                                .setName(String.valueOf(i));
                        logTest.setName2(String.valueOf(i));
                        logTest.setName3(String.valueOf(i));
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
    public void select() {
        long nanoTime = System.nanoTime();
        String string = String.valueOf(RandomUtils.random(1, 10000));
        List<MysqlLogTest> all2Stream = jdbcContext.findAll(
                "from " + MysqlLogTest.class.getSimpleName() + " where json_extract(sensors,'$.e.aa') = ?1",
                MysqlLogTest.class,
                string
        );
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select $a=" + string + " - count = " + all2Stream.size());
    }

    @Test
    @RepeatedTest(5)
    public void selectc() {
        long nanoTime = System.nanoTime();
        String string = String.valueOf(RandomUtils.random(1, 10000));
        Stream<MysqlLogTest> all2Stream = jdbcContext.findAll2Stream(
                "from " + MysqlLogTest.class.getSimpleName() + " where json_extract(sensors,'$.c') = ?1",
                MysqlLogTest.class,
                string
        );
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        System.out.println("select $c=" + string + " - count = " + all2Stream.count());
    }


}
