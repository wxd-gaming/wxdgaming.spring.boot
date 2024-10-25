package code;

import io.protostuff.ProtostuffIOUtil;
import org.junit.Test;
import test.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.net.message.ProtoBuf2Pojo;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 10:41
 **/
public class StringTest {

    @Test
    public void t0() {
        ProtoBuf2Pojo.actionProtoFile("./src/main/java", "./src/main");
    }

    @Test
    public void t11() {
        InnerMessage.ReqRegister.Builder builder = InnerMessage.ReqRegister.newBuilder();
        builder.setStype(InnerMessage.Stype.CHAT);
        builder.setSid(2);
        builder.setWlanIp("10.219.20.2");
        InnerMessage.ReqRegister build = builder.build();
        System.out.println(build.toString());
        byte[] byteArray = build.toByteArray();
        t12(byteArray);
    }

    public void t12(byte[] byteArray) {
        wxdgaming.spring.boot.broker.pojo.inner.InnerMessage.ReqBrokerRegister parseFrom = new wxdgaming.spring.boot.broker.pojo.inner.InnerMessage.ReqBrokerRegister();
        parseFrom.decode(byteArray);
        System.out.println(parseFrom);
    }

}
