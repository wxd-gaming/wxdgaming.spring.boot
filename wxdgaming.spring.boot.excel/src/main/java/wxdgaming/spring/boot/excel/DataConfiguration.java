package wxdgaming.spring.boot.excel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.excel.store.DataRepository;

/**
 * guice 注册模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-09 14:00
 **/
@Configuration
public class DataConfiguration implements InitPrint {

    @Bean
    protected DataRepository dataRepository() {
        return DataRepository.getIns();
    }

    @Bean
    protected ExcelRepository excelRepository() {
        return ExcelRepository.getIns();
    }

}
