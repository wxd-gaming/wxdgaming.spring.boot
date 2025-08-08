package code;

import org.junit.jupiter.api.Test;
import wxdgaming.spring.logserver.bean.LogField;
import wxdgaming.spring.logserver.bean.LogMappingInfo;

public class OutLogMappingTest {

    @Test
    public void t1() {
        LogMappingInfo logMappingInfo = new LogMappingInfo();
        logMappingInfo.setLogName("test");
        logMappingInfo.setPartition(true);
        logMappingInfo.getFieldList().add(new LogField().setFieldName("openId").setFieldComment("账号Id").setFieldType("string"));
        logMappingInfo.getFieldList().add(new LogField().setFieldName("account").setFieldComment("账号").setFieldType("string"));
        System.out.println(logMappingInfo.toJSONStringAsFmt());
    }

}
