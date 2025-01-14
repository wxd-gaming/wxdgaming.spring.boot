package wxdgaming.spring.boot.data.batis;

import com.mongodb.ConnectionString;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

/**
 * data-mongo 组件扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-05 13:51
 **/
@Configuration
public class MongoBuild {

    @Primary
    @Bean()
    @ConfigurationProperties(prefix = "spring.data.mongodb")
    public MongoProperties mongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean("mongoDatabaseFactory")
    public MongoDatabaseFactory mongoDatabaseFactory(@Qualifier("mongoProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(new ConnectionString(mongoProperties.determineUri()));
    }

    @Primary
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate(@Qualifier("mongoDatabaseFactory") MongoDatabaseFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }


}
