package code;

import wxdgaming.spring.boot.data.excel.ExcelRepository;

import java.io.File;

/**
 * 读取测试
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-09 19:29
 **/
public class ReadTest {

    public void t0(String[] args) {
        ExcelRepository excelReader = new ExcelRepository();
        excelReader.readExcel(new File("data-excel/src/main/resources/范例.xlsx"));
        excelReader.getTableInfoMap().values().forEach(tableInfo -> {
            System.out.println(tableInfo.showData());
            Object shopItem = tableInfo.getObject(2, "shop_item");
            System.out.println(tableInfo.getString(2, "shop_item"));
            System.out.println(tableInfo.getString(2, "show_time"));

            System.out.println(tableInfo.data2Json());

        });
    }

}
