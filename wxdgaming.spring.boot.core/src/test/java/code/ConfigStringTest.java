package code;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.ConfigString;

public class ConfigStringTest {

    @Test
    public void t1() {
        ConfigString configString = FastJsonUtil.parse("{\"cfg\":\"1:2\"}", ConfigString.class);
        System.out.println(JSON.toJSONString(configString));
    }

    @Getter
    @Setter
    static class CS1 {
        private ConfigString cfg;

        @Override public String toString() {
            return cfg.toString();
        }
    }

}
