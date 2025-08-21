package code.rank;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.executor.ExecutorServicePlatform;
import wxdgaming.spring.boot.core.lang.DiffTime;
import wxdgaming.spring.boot.core.rank.RankByTreeSet;
import wxdgaming.spring.boot.core.rank.RankScore;
import wxdgaming.spring.boot.core.util.RandomUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 测试
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-20 21:08
 **/
@Slf4j
public class Rank2Test {

    public static void main(String[] args) {
        RankByTreeSet rankMap = new RankByTreeSet();
        ExecutorServicePlatform executorService = ExecutorFactory.create("map", 3);
        for (int k = 0; k < 10; k++) {
            executorService.execute(() -> {
                StringBuilder stringBuilder = new StringBuilder();

                DiffTime diffTime = new DiffTime();
                int iCount = 1000;
                int maxRandom = 5000;
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                stringBuilder.append("插入 " + iCount + " 对象 " + diffTime.diffUs5() + "us").append("\n");
                diffTime.reset();
                for (int i = 0; i < iCount; i++) {
                    rankMap.updateScore(String.valueOf(i + 1), RandomUtils.random(1, maxRandom));
                }
                stringBuilder.append("修改 " + iCount + " 对象 " + diffTime.diffUs5() + "us").append("\n");
                String random = String.valueOf(RandomUtils.random(1, iCount));
                {
                    diffTime.reset();
                    rankMap.updateScore(random, RandomUtils.random(1, maxRandom));
                    stringBuilder.append("随机修改一个对象 " + random + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                {
                    diffTime.reset();
                    int rank = rankMap.rank(random);
                    stringBuilder.append("随机读取一个对象 " + random + " 排名 " + rank + " - " + diffTime.diffUs5() + "us").append("\n");
                    diffTime.reset();
                    RankScore rankScore = rankMap.rankDataByRank(rank);
                    stringBuilder.append("随机读取一个排名 " + rank + " 对象 " + rankScore.getKey() + " - " + diffTime.diffUs5() + "us").append("\n");
                }
                diffTime.reset();
                rankMap.rankBySize(100);
                stringBuilder.append("返回前 100 名 " + diffTime.diffUs5() + "us").append("\n");
                stringBuilder.append("=========================================").append("\n");
                System.out.println(stringBuilder.toString());
            });
        }
        // for (RankScore rankScore : topN) {
        //     log.info("{}", rankScore);
        // }

        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
        System.exit(0);
    }

}
