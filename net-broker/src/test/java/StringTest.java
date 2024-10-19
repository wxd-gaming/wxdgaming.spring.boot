import org.junit.Test;
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
    public void t1() {
        String tmp = """
                int a = %d;
                 string b = "%s";
                """.formatted(3, "g");
        System.out.println(tmp);
    }

}
