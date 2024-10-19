package wxdgaming.spring.boot.data.excel.store;

import wxdgaming.spring.boot.data.excel.TableData;

public interface ICreateCode {

    void createCode(TableData tableData, String outPath, String packageName, String belong);

}
