package wxdgaming.spring.boot.core.format;

import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * id算法
 * <p>因为无符号 所以每一秒的id最大值是52万
 * <p>hexId 取值范围 1 ~ 16500
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-24 15:08
 */
public class LogId implements Serializable {

    /** 相当于1970年1月1日，到2024年9月24日 经过了这么多天 */
    public static final int OffSetDays = 19990;
    /** 19位的最大值 */
    public int ID_MAX = 524287;

    final long hexId;
    volatile long lastDays = 0;
    volatile long lastSecondByDay = 0;
    volatile long seed = 0;

    public static void main(String[] args) throws InterruptedException {

        System.out.println(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()));

        System.out.println(Long.MAX_VALUE);
        System.out.println((4000 << 17 | 86400));

        final LogId newId = new LogId(1500);
        {
            final long id = newId.newId();
            System.out.println(id);
        }

        Set<Long> ids = new HashSet<>();
        for (int k = 0; k < 6; k++) {

            for (int i = 0; i < 50_0000; i++) {
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

    public LogId(long hexId) {
        AssertUtil.assertTrue(0 < hexId && hexId < 16501, "取值范围 1 ~ 16500");
        this.hexId = hexId;
    }

    public synchronized long newId() {
        final long days = MyClock.days() - OffSetDays;
        final long secondByDay = MyClock.dayOfSecond();
        if (lastDays != days || secondByDay != lastSecondByDay) {
            seed = 0;
            lastDays = days;
            lastSecondByDay = secondByDay;
        }

        /*因为无符号 所以每一秒的id最大值是52万*/
        seed++;

        if (seed > ID_MAX) {
            throw new RuntimeException("每秒钟生成的最大值 " + ID_MAX);
        }
        //   hexId 占15位     day 占用12位  second 占17位       seed 占用19位
        return hexId << 48 | days << 36 | secondByDay << 19 | seed;
    }


}
