package code;

import code.entity.TestIndex;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.data.batis.DataMongoScan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-01-13 15:22
 **/
@RunWith(SpringRunner.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootTest(classes = {CoreScan.class, DataMongoScan.class, MongoTest.class})
public class MongoTest {

   static HexId hexId = new HexId(1);
    @Autowired MongoTemplate mongoTemplate;
    @Autowired @Qualifier("secondaryMongoTemplate") MongoTemplate mongoTemplate2;

    @Test
    @RepeatedTest(5)
    public void add() {
        for (int k = 0; k < 10; k++) {
            long nanoTime = System.nanoTime();
            List<TestIndex> list = new ArrayList<>();
            for (int i = 0; i < 2000; i++) {
                TestIndex entity = new TestIndex();
                entity.setId(hexId.newId());
                entity.setIndex(1);
                entity.setUsername(String.valueOf(System.nanoTime()));
                entity.setEmail(String.valueOf(System.nanoTime()));
                list.add(entity);
            }
            mongoTemplate.insertAll(list);
            // mongoTemplate2.insertAll(list);
            System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        }
    }

    @Test
    @RepeatedTest(5)
    public void count() {
        System.out.println("hashCode: " + hexId.hashCode());
        long nanoTime = System.nanoTime();
        MongoCollection<Document> collection = mongoTemplate.createCollection(TestIndex.class);
        ArrayList<Bson> arrayList = new ArrayList<>();
        arrayList.add(new Document("$match", new Document("username", "91418914685800")));
        arrayList.add(new Document("$count", "count"));
        AggregateIterable<Document> aggregate = collection.aggregate(arrayList);
        System.out.println((System.nanoTime() - nanoTime) / 10000 / 100f + " ms");
        for (Document document : aggregate) {
            System.out.println(document);
        }
    }

}
