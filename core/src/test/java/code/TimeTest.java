package code;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-01-06 16:46
 **/
public class TimeTest {

    @Test
    public void t0() {
        show(LocalDate.of(2024, 12, 28));
        show(LocalDate.of(2024, 12, 29));
        show(LocalDate.of(2024, 12, 30));
        show(LocalDate.of(2024, 12, 31));
        show(LocalDate.of(2025, 1, 1));
        show(LocalDate.of(2025, 1, 2));
        show(LocalDate.of(2025, 1, 6));
    }

    public void show(LocalDate today) {
        // 设置星期的起始日为星期一（默认是星期日）
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        // 获取当前日期是今年的第几周
        int weekOfYear = today.get(weekFields.weekOfWeekBasedYear());
        System.out.println("时间 " + today + " 是今年的第 " + weekOfYear + " 周");
    }

}
