package wxdgaming.spring.logserver.module.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.io.FileReadUtil;
import wxdgaming.spring.logserver.bean.LogMappingInfo;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 系统服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-09 18:02
 **/
@Slf4j
@Service
public class SystemService implements InitPrint {

    List<LogMappingInfo> logMappingInfoList;

    public SystemService() {
        String json = FileReadUtil.readString("log-init.json", StandardCharsets.UTF_8);
        logMappingInfoList = FastJsonUtil.parseArray(json, LogMappingInfo.class);
    }

}
