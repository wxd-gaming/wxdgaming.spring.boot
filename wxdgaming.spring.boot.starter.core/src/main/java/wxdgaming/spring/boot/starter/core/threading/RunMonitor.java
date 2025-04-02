package wxdgaming.spring.boot.starter.core.threading;

/**
 * 运行时间监控
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-06-24 19:09
 **/
public interface RunMonitor {
    /** 输出日志的时间 */
    default long getLogTime() {return 33;}

    /** 执行告警时间 */
    default long getWarningTime() {return 1000;}
}
