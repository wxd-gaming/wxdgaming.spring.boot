package wxdgaming.spring.boot.starter.data.excel.store;

import wxdgaming.spring.boot.starter.data.excel.TableData;

public interface ICreateCode {

    void createCode(TableData tableData, String outPath, String packageName, String belong);

}
