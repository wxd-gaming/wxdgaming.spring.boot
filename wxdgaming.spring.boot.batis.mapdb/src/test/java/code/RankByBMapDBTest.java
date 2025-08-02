package code;

import org.junit.jupiter.api.*;
import wxdgaming.spring.boot.batis.mapdb.HoldMap;
import wxdgaming.spring.boot.batis.mapdb.MapDBDataHelper;
import wxdgaming.spring.boot.core.format.data.Data2Size;
import wxdgaming.spring.boot.core.lang.DiffTime;
import wxdgaming.spring.boot.core.rank.RankScore;
import wxdgaming.spring.boot.core.util.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RankByBMapDBTest {

    static final MapDBDataHelper mapDBDataHelper = new MapDBDataHelper("target/btreerank.db");
    static final HoldMap holdMap = mapDBDataHelper.bMap("lv-rank");

    @Order(1)
    @Test
    public void aputRank() {

        holdMap.clear();

        for (int i = 0; i < 100000; i++) {
            String k = String.valueOf(RandomUtils.random(1, Long.MAX_VALUE));
            RankScore rankScore = new RankScore().setKey(k);
            rankScore.setTimestamp(System.nanoTime());
            rankScore.setScore(RandomUtils.random(1, 5000));
            holdMap.put(k, rankScore);
        }

    }

    @Order(2)
    @RepeatedTest(10)
    public void b1treeSet() {
        DiffTime diffTime = new DiffTime();
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        System.out.println("读取耗时：" + diffTime.diffMs5AndReset());
        TreeSet<RankScore> rankScores = new TreeSet<>();
        for (Object value : objects) {
            RankScore rankScore = (RankScore) value;
            rankScores.add(rankScore);
        }
        System.out.println("排序耗时：" + diffTime.diffMs5AndReset() + ", " + holdMap.size());
        List<RankScore> list = rankScores.stream().limit(20).toList();

        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(2)
    @RepeatedTest(10)
    public void b2skipSet() {
        DiffTime diffTime = new DiffTime();
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        System.out.println("读取耗时：" + diffTime.diffMs5AndReset());
        ConcurrentSkipListSet<RankScore> rankScores = new ConcurrentSkipListSet<>();
        for (Object value : objects) {
            RankScore rankScore = (RankScore) value;
            rankScores.add(rankScore);
        }
        System.out.println("排序耗时：" + diffTime.diffMs5AndReset() + ", " + holdMap.size());
        List<RankScore> list = rankScores.stream().limit(20).toList();

        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(3)
    @RepeatedTest(10)
    public void clistSort() {
        DiffTime diffTime = new DiffTime();
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        System.out.println("读取耗时：" + diffTime.diffMs5AndReset());
        List<RankScore> list = objects.stream().map(value -> (RankScore) value).sorted().limit(20).toList();
        System.out.println("排序耗时：" + diffTime.diffMs5AndReset() + ", " + holdMap.size());
        System.out.println("==============================");
        //        float v = diffTime.diffMs5();
        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(4)
    @RepeatedTest(10)
    public void darraySort() {
        DiffTime diffTime = new DiffTime();
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        System.out.println("读取耗时：" + diffTime.diffMs5AndReset());
        RankScore[] array = objects.toArray(new RankScore[0]);
        Arrays.sort(array);
        System.out.println("排序耗时：" + diffTime.diffMs5AndReset() + ", " + array.length);
        System.out.println("==============================");

        //        for (RankScore rankScore : list) {
        //            System.out.println(rankScore);
        //        }
    }

    @Order(99994)
    @Test
    public void memery() {
        DiffTime diffTime = new DiffTime();
        ArrayList<Object> objects = new ArrayList<>(holdMap.values());
        String string = Data2Size.totalSizes0(objects);
        System.out.println("长度：" + objects.size() + "内存占用：" + string);
    }

}
