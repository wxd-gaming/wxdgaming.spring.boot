package run;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import wxdgaming.spring.boot.net.pojo.ProtoBuf2Pojo;

/**
 * protobuf篡改
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-27 13:15
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProtoTest {

    @Test
    @Order(1)
    public void buildProtoBuf() {
        ProtoBuf2Pojo.actionProtoFile("src/main/java", "src/main/proto-inner");
        ProtoBuf2Pojo.actionProtoFile("src/main/java", "src/main/proto");
    }


}
