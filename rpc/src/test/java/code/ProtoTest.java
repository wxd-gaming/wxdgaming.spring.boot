package code;

import io.protostuff.Tag;
import lombok.Data;
import org.junit.Test;
import wxdgaming.spring.boot.message.ProtoBufPojo;
import wxdgaming.spring.boot.message.SerializerUtil;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.util.Arrays;

/**
 * protobuf篡改
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-27 13:15
 **/
public class ProtoTest {

    @Test
    public void t0() {
        ProtoBufPojo.actionProtoFile("./src/main/java", "./src/main");
    }

    @Test
    public void t1() {
        RpcMessage.ReqRemote builder = new RpcMessage.ReqRemote();
        builder.setRpcId(1);
        builder.setGzip(1);
        builder.setCmd("ss");
        builder.setParams("1");
        builder.setRpcToken("1");
        byte[] encode1 = SerializerUtil.encode(builder);
        Tq tq = new Tq();
        byte[] encode = SerializerUtil.encode(tq);
        System.out.println(Arrays.toString(encode1));
        System.out.println(Arrays.toString(encode));
        System.out.println("1");
    }

    @Data
    public static class Tq {
        @Tag(1)
        private long rpcId = 1;
        @Tag(2)
        private int gzip = 1;
        @Tag(3)
        private String cmd = "ss";
        @Tag(4)
        private String params = "1";
        @Tag(5)
        private String rpcToken = "1";

    }

}
