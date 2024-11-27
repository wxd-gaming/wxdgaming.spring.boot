package wxdgaming.spring.boot.data.batis;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.function.ConsumerE1;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据源配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-02 21:19
 **/
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties("spring")
@ConditionalOnProperty("spring.db.url")
public class DbHelper implements InitPrint {

    DruidSourceConfig db;

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource datasource() {
        db.createDatabase();
        return db.toDataSource();
    }

    public void queryJsonObject(DataSource dataSource, String query, ConsumerE1<JSONObject> consumer) throws Exception {
        query0(dataSource, query, resultSet -> {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int j = 1; j < columnCount + 1; j++) {
                JSONObject jsonObject = new JSONObject();
                Object object = resultSet.getObject(j);
                String columnName = resultSet.getMetaData().getColumnLabel(j);
                jsonObject.put(columnName, object);
                consumer.accept(jsonObject);
            }
        });
    }

    public void query0(DataSource dataSource, String query, ConsumerE1<ResultSet> consumer) throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(query);) {
                while (resultSet.next()) {
                    consumer.accept(resultSet);
                }
            }
        }
    }

}
