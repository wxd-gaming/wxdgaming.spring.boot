package wxdgaming.spring.boot.core.rank;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分散型排行榜容器, 如果要序列化存储到数据库中，请调用tolist方案
 * <br>
 * 利用set的自动排序功能，每次更新分数时，会自动排序
 * <br>
 * 分散型排行榜容器，比较适合战力排行榜，分数排行榜，竞技场排行榜
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-20 20:57
 **/
@Getter
@Setter
public class RankByTreeSet {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private final HashMap<String, RankScore> map = new HashMap<>();
    private final TreeSet<RankScore> rankTreeSet = new TreeSet<>();

    public RankByTreeSet() {

    }

    public RankByTreeSet(List<RankScore> rankScores) {
        push(rankScores);
    }

    /** 所有的排行 */
    private void push(List<RankScore> rankScores) {
        writeLock.lock();
        try {
            rankScores.forEach(rankScore -> {
                RankScore oldRankScore = map.get(rankScore.getKey());
                long oldScore = oldRankScore.getScore();
                if (oldScore != rankScore.getScore()) {
                    rankTreeSet.remove(oldRankScore);
                    // 插入新的ScoreKey
                    rankTreeSet.add(rankScore);
                }
            });
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 更新用户分数
     *
     * @param key      用户ID
     * @param newScore 新的分数
     */
    public RankScore updateScore(String key, long newScore) {
        return updateScore(key, newScore, System.nanoTime());
    }

    /**
     * 更新用户分数
     *
     * @param key       key
     * @param newScore  分数
     * @param timestamp 时间戳,建议使用 {@link System#nanoTime()}
     */
    public RankScore updateScore(String key, long newScore, long timestamp) {
        writeLock.lock();
        try {
            RankScore rankScore = map.computeIfAbsent(key, k -> new RankScore().setKey(key));
            long oldScore = rankScore.getScore();
            if (oldScore != newScore) {
                rankTreeSet.remove(rankScore);

                rankScore.setScore(newScore);
                rankScore.setTimestamp(timestamp);
                // 插入新的ScoreKey
                rankTreeSet.add(rankScore);
            }
            return rankScore;
        } finally {
            writeLock.unlock();
        }
    }

    public int rank(String key) {
        readLock.lock();
        try {
            RankScore rankScore = map.get(key);
            if (rankScore == null) {
                return -1;
            }
            //                        SortedSet<RankScore> rankScores = rankMap.headSet(rankScore);
            //                        if (!rankScores.isEmpty()) {
            //                            return rankScores.size() + 1;
            //                        }
            int rank = 0;
            for (RankScore value : rankTreeSet) {
                rank++;
                if (value.getKey().equals(key)) {
                    return rank;
                }
            }
            return -1;
        } finally {
            readLock.unlock();
        }
    }

    public long score(String key) {
        RankScore rankScore = map.get(key);
        if (rankScore == null) {
            return -1;
        }
        return rankScore.getScore();
    }

    public RankScore rankData(String key) {
        return map.get(key);
    }

    public RankScore rankDataByRank(final int rank) {
        readLock.lock();
        try {
            AssertUtil.assertTrue(rank > 0, "rank must be greater than 0");
            if (map.size() < rank) {
                return null;
            }
            int currentRank = 0;
            for (RankScore value : rankTreeSet) {
                currentRank++;
                if (currentRank >= rank) {
                    return value;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public List<RankScore> rankByRange(int startRank, int endRank) {
        return rankByRange(startRank, true, endRank, true);
    }

    public List<RankScore> rankByRange(int startRank, boolean hasStart, int endRank, boolean hasEnd) {
        readLock.lock();
        try {
            ArrayList<RankScore> rankScores = new ArrayList<>(endRank - startRank + 1);
            int currentRank = 0;
            for (RankScore rankScore : rankTreeSet) {
                currentRank++;
                if (currentRank < startRank) {
                    continue;
                }
                if (currentRank == startRank && !hasStart) {
                    continue;
                }
                rankScores.add(rankScore);
                if (currentRank > endRank || (currentRank == endRank && !hasEnd)) {
                    return rankScores;
                }
            }
            return rankScores;
        } finally {
            readLock.unlock();
        }
    }

    /** 返回前多少名 */
    public List<RankScore> rankBySize(int n) {
        readLock.lock();
        try {
            if (n <= 0) {
                return Collections.emptyList();
            }
            if (map.isEmpty()) {
                return List.of();
            }
            ArrayList<RankScore> rankScores = new ArrayList<>(n);
            for (RankScore rankScore : rankTreeSet) {
                rankScores.add(rankScore);
                if (rankScores.size() >= n) {
                    return rankScores;
                }
            }
            return rankScores;
        } finally {
            readLock.unlock();
        }
    }

    public List<RankScore> toList() {
        return rankBySize(rankTreeSet.size());
    }

}
