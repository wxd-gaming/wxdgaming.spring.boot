package wxdgaming.spring.boot.core.format;

import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.io.Serializable;

/**
 * id算法
 * <p>因为无符号 所以每一秒的id最大值是52万
 * <p>hexId 取值范围 1 ~ 16500
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-24 15:08
 */
public class HexId implements Serializable {

    /** 相当于1970年1月1日，到2024年9月24日 经过了这么多天 */
    public static final int OffSetDays = 19990;
    /** 19位的最大值 */
    public static final int Offset12 = 0xFFF;
    public static final int Offset14 = 0x3FFF;
    public static final int Offset17 = 0x1FFFF;
    public static final long Offset20 = 0XFFFFF;

    final long hexId;
    volatile long lastDays = 0;
    volatile long lastSecondByDay = 0;
    volatile long seed = 0;

    public HexId(long hexId) {
        AssertUtil.assertTrue(0 < hexId && hexId < Offset14, "取值范围 1 ~ " + Offset14);
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

        if (seed > Offset20) {
            throw new RuntimeException("每秒钟生成的最大值 " + Offset20);
        }
        //   hexId 占14位     day 占用12位  second 占17位       seed 占用19位
        long lid = hexId << 49;
        lid |= days << 37;
        lid |= secondByDay << 20;
        lid |= seed;
        int i = secondByDay(lid);
        long idValue = idValue(lid);
        if (i != secondByDay || idValue != seed) {
            throw new RuntimeException("id=" + lid + ", hexId=" + hexId + ", secondByDay=" + secondByDay + ", seed=" + seed);
        }
        return lid;
    }

    public static int hexId(long value) {
        return (int) (value >> 49 & Offset14);
    }

    public static int days(long value) {
        return (int) (value >> 37 & Offset12);
    }

    public static int secondByDay(long value) {
        return (int) (value >> 20 & Offset17);
    }

    public static long idValue(long value) {
        return (value & Offset20);
    }

}
