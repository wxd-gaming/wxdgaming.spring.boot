package wxdgaming.spring.boot.scheduled;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.ann.Shutdown;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.executor.ExecutorConfig;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.executor.ExecutorServicePlatform;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务调度器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-09-24 18:21
 **/
@Slf4j
@Getter
public class ScheduledService {

    protected ScheduledFuture<?> future;
    /*                          类名字                  方法名    实例 */
    protected List<ScheduledInfo> jobList = new ArrayList<>();

    protected final ExecutorConfig config;
    protected ExecutorServicePlatform executorServicePlatform;

    public ScheduledService(ExecutorConfig config) {
        executorServicePlatform = ExecutorFactory.create("scheduled-executor", config);
        this.config = config;
    }

    @Init
    public void init(ApplicationContextProvider runApplication) {
        log.debug("------------------------------初始化定时任务调度器------------------------------");
        List<ScheduledInfo> tmpJobList = new ArrayList<>();
        runApplication.withMethodAnnotated(Scheduled.class)
                .forEach(methodContent -> {
                    ScheduledInfo scheduledInfo = new ScheduledInfo(
                            methodContent.getBean(),
                            methodContent.getMethod(),
                            methodContent.getMethod().getAnnotation(Scheduled.class)
                    );
                    log.debug("Scheduled job {}", methodContent.getMethod());
                    tmpJobList.add(scheduledInfo);
                });
        sort(tmpJobList);
        jobList = tmpJobList;
    }

    @Start
    @Order(99999998)
    public void start() {
        ScheduleTrigger scheduleTrigger = new ScheduleTrigger();
        future = executorServicePlatform.scheduleAtFixedRate(
                scheduleTrigger,
                10,
                10,
                TimeUnit.MILLISECONDS
        );
    }

    @Shutdown
    @Order(1000)
    public void shutdown() {
        log.info("线程 Scheduled 调度器 退出");
        if (future != null) {
            future.cancel(true);
            future = null;
        }
    }

    public void sort(List<ScheduledInfo> jobs) {
        jobs.sort(Comparator.comparingLong(ScheduledInfo::getNextRunTime));
    }


    /** 触发器 */
    protected class ScheduleTrigger extends ExecutorEvent {

        public ScheduleTrigger() {
            super();
        }

        @Override public String queueName() {
            return "scheduled-timer";
        }

        int curSecond = -1;

        @Override public void onEvent() {
            long millis = MyClock.millis();
            int second = MyClock.getSecond(millis);
            if (curSecond == second) {
                return;
            }
            curSecond = second;
            boolean needSort = false;
            for (ScheduledInfo scheduledInfo : jobList) {
                if (!scheduledInfo.checkRunTime(millis)) {
                    break;
                }
                if (runJob(scheduledInfo, millis)) {
                    needSort = true;
                }
            }
            if (needSort) {
                sort(jobList);
            }
        }

        public boolean runJob(ScheduledInfo scheduledInfo, long millis) {
            scheduledInfo.lock.lock();
            try {
                if (!scheduledInfo.isScheduleAtFixedRate() && !scheduledInfo.runEnd.get())
                    return false;
                /*标记为正在执行*/
                scheduledInfo.runEnd.set(false);
                scheduledInfo.nextRunTime = scheduledInfo.getCronExpress().validateTimeAfterMillis();
            } finally {
                scheduledInfo.lock.unlock();
            }

            if (scheduledInfo.isAsync()) {
                /*异步执行*/
                scheduledInfo.submit();
            } else {
                /*同步执行*/
                scheduledInfo.run();
            }
            return true;
        }

    }
}
