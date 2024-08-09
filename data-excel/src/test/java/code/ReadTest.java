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

    public static void main(String[] args) {
        ExcelRepository excelReader = new ExcelRepository();
        excelReader.builderWorkbook(new File("data-excel/src/main/resources/范例.xlsx"));
        excelReader.getTableInfoMap().values().forEach(tableInfo -> {
            System.out.println(tableInfo.showData());
            System.out.println(tableInfo.getString(2, "shop_item"));
            System.out.println(tableInfo.getString(2, "show_time"));
        });
    }

}