package code;

import code.db1.Log1;
import code.db1.Log1Jpa;
import code.db2.Log2;
import code.db2.Log2Jpa;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.format.HexId;
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
@SpringBootApplication
@EntityScan(basePackages = {"code.db1"})
@EnableJpaRepositories("code.db1")
@SpringBootTest(classes = {CoreScan.class, DataJdbcScan.class})
public class SpringJdbcTest {

    HexId hexId = new HexId(1);
    @Autowired JdbcContext jdbcContext;
    @Autowired Log1Jpa log1Repository;
    @Qualifier("jdbcContext2")
    @Autowired(required = false) JdbcContext jdbcContext2;
    @Autowired(required = false) Log2Jpa log2Repository;

    @Test
    public void insert() {
        IntStream.range(1, 10000).parallel()
                .forEach(k -> {
                    long nanoTime = System.nanoTime();
                    List<Log1> logs = new ArrayList<>();
                    for (int i = 0; i < 2000; i++) {
                        logs.add(new Log1().setUid(hexId.newId()).setName(String.valueOf(i)));
                    }
                    jdbcContext.batchInsert(logs);
                    System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
                });
    }

    @Test
    public void insert2() {
        jdbcContext2.save(new Log2().setUid(hexId.newId()));
        log.info("end");
    }


}
