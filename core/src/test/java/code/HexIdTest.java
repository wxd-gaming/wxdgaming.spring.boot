package code;

import org.junit.Test;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.core.timer.MyClock;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class HexIdTest {


    @Test
    public void t10() throws Exception {
        System.out.println(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));

        System.out.println(Long.MAX_VALUE);
        System.out.println((4000 << 17 | 86400));

        final HexId newId = new HexId(2001);
        {
            final long id = newId.newId();
            System.out.println(id);
        }

        Set<Long> ids = new HashSet<>();
        for (int k = 0; k < 6; k++) {

            for (int i = 0; i < 500_0000; i++) {
                final long id = newId.newId();
                if (!ids.add(id)) {
                    System.out.println("重复id " + id + " " + new Date());
                    System.out.println("结束 " + ids.size());
                    return;
                }
                //                log.debug("{}", id);
            }
            Thread.sleep(1000);
        }
    }

    @Test
    public void t11() throws Exception {
        final HexId newId = new HexId(16300);
        System.out.println(HexId.Offset14);
        System.out.println(HexId.Offset20);
        System.out.println(Integer.toBinaryString(HexId.Offset14));
        System.out.println(MyClock.dayOfSecond());
        long lid = newId.newId();
        show(lid);

    }

    @Test
    public void t12() throws Exception {
        show(1126463192087658497L);
    }

    void show(long lid) {
        System.out.printf("%16s - %s\n", "longMax", Long.MAX_VALUE);
        System.out.printf("%16s - %s\n", "Id", lid);
        System.out.printf("%16s - %s\n", "hexId", HexId.hexId(lid));
        System.out.printf("%16s - %s\n", "days", HexId.days(lid));
        System.out.printf("%16s - %s\n", "secondByDay", HexId.secondByDay(lid));
        System.out.printf("%16s - %s\n", "value", HexId.idValue(lid));
    }

    @Test
    public void t20() {
        long x = 183625L << 20;
        System.out.println(x);
        System.out.println(x >> 20);
        HexId.secondByDay(183625);
        System.out.println(TimeUnit.SECONDS.toHours(57615));
    }

}
