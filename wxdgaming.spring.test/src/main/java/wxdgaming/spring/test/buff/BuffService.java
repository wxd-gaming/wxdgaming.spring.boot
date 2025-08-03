package wxdgaming.spring.test.buff;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.test.map.MapObject;

/**
 * buff 管理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 20:55
 **/
@Slf4j
@Getter
@Service
public class BuffService implements InitPrint {

    private final HexId hexId = new HexId(1);

    @PostConstruct
    public void initializeBuffTemplates() {
        
    }

    public void execute(MapObject attack) {


    }

}
