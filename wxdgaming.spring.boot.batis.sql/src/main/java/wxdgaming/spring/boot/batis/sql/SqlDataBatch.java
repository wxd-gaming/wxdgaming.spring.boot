package wxdgaming.spring.boot.batis.sql;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.batis.DataBatch;
import wxdgaming.spring.boot.batis.Entity;
import wxdgaming.spring.boot.batis.TableMapping;
import wxdgaming.spring.boot.core.ann.Shutdown;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.collection.ConvertCollection;
import wxdgaming.spring.boot.core.collection.SplitCollection;
import wxdgaming.spring.boot.core.collection.Table;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.io.FileWriteUtil;
import wxdgaming.spring.boot.core.lang.DiffTime;
import wxdgaming.spring.boot.core.lang.Tick;
import wxdgaming.spring.boot.core.util.GlobalUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * sql 模型 批量 处理
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-16 20:43
 **/
@Slf4j
public abstract class SqlDataBatch extends DataBatch {

    protected final SqlDataHelper sqlDataHelper;
    protected final List<BatchThread> batchThreads = new ArrayList<>();

    public SqlDataBatch(SqlDataHelper sqlDataHelper) {
        this.sqlDataHelper = sqlDataHelper;
        int batchThreadSize = sqlDataHelper.getSqlConfig().getBatchThreadSize();
        log.info("{} 数据库: {} 创建 {} 个 sql 批量线程", sqlDataHelper.getClass().getSimpleName(), sqlDataHelper.getDbName(), batchThreadSize);
        for (int i = 1; i <= batchThreadSize; i++) {
            batchThreads.add(new BatchThread(i, "Sql-Batch-" + sqlDataHelper.getDbName() + "-" + i, sqlDataHelper.getSqlConfig().getBatchSubmitSize()));
        }
    }

    @Shutdown
    public void close() {
        for (BatchThread batchThread : batchThreads) {
            log.info("准备关闭线程 {}", batchThread);
            batchThread.closed.set(true);
        }
        for (BatchThread batchThread : batchThreads) {
            try {
                batchThread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    public <SDH extends SqlDataHelper> SDH dataHelper() {
        return (SDH) sqlDataHelper;
    }

    @Override public void save(Entity entity) {
        Boolean newEntity = entity.getNewEntity();
        if (newEntity == null) {
            newEntity = !this.sqlDataHelper.existBean(entity);
        }
        if (newEntity)
            insert(entity);
        else
            update(entity);
        entity.setNewEntity(false);
    }

    BatchThread batchThread(Entity entity) {
        int hashCode = entity.hashCode();
        int index = Math.abs(hashCode) % batchThreads.size();
        return batchThreads.get(index);
    }

    @Override public void insert(Entity entity) {
        batchThread(entity).insert(entity);
    }

    @Override public void update(Entity entity) {
        batchThread(entity).update(entity);
    }

    public class BatchThread extends Thread {

        protected final ReentrantLock lock = new ReentrantLock();
        protected AtomicBoolean closed = new AtomicBoolean();
        protected final int threadId;
        protected final int batchSubmitSize;
        /** key: tableName, value: {key: sql, value: params} */
        protected Table<String, String, ConvertCollection<BatchParam>> batchInsertMap = new Table<>();
        /** key: tableName, value: {key: sql, value: params} */
        protected Table<String, String, ConvertCollection<BatchParam>> batchUpdateMap = new Table<>();

        protected DiffTime diffTime = new DiffTime();
        protected long executeDiffTime = 0;
        protected long executeCount = 0;
        protected Tick ticket = new Tick(1, TimeUnit.MINUTES);

        public BatchThread(int threadId, String name, int batchSubmitSize) {
            super(name);
            this.threadId = threadId;
            this.batchSubmitSize = batchSubmitSize;
            this.setPriority(MAX_PRIORITY);
            this.start();
        }

        public void insert(Entity entity) {
            String tableName = TableMapping.beanTableName(entity);
            TableMapping tableMapping = sqlDataHelper.tableMapping(entity.getClass());
            String insertSql = sqlDataHelper.ddlBuilder().buildInsertSql(tableMapping, tableName);

            Object[] keyParams = sqlDataHelper.ddlBuilder().buildKeyParams(tableMapping, entity);
            Object[] insertParams = sqlDataHelper.ddlBuilder().buildInsertParams(tableMapping, entity);
            BatchParam batchParam = new BatchParam(entity, keyParams, insertParams);
            lock.lock();
            try {
                ConvertCollection<BatchParam> collection = batchInsertMap.computeIfAbsent(tableName, insertSql, k -> new ConvertCollection<>());
                collection.add(batchParam);
                entity.setNewEntity(false);
            } finally {
                lock.unlock();
            }
        }

        public void update(Entity entity) {
            String tableName = TableMapping.beanTableName(entity);
            TableMapping tableMapping = sqlDataHelper.tableMapping(entity.getClass());
            String updateSql = sqlDataHelper.ddlBuilder().buildUpdateSql(tableMapping, tableName);

            Object[] keyParams = sqlDataHelper.ddlBuilder().buildKeyParams(tableMapping, entity);
            Object[] updateParams = sqlDataHelper.ddlBuilder().builderUpdateParams(tableMapping, entity);
            BatchParam batchParam = new BatchParam(entity, keyParams, updateParams);
            lock.lock();
            try {
                ConvertCollection<BatchParam> collection = batchUpdateMap.computeIfAbsent(tableName, updateSql, k -> new ConvertCollection<>());
                collection.add(batchParam);
            } finally {
                lock.unlock();
            }
        }

        @Override public void run() {
            BatchJob batchJob = new BatchJob();
            while (true) {
                if (!closed.get() && !GlobalUtil.Exiting.get()) {
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
                }
                batchJob.run();
                if (closed.get()) {
                    if (batchInsertMap.isEmpty() && batchUpdateMap.isEmpty())
                        break;
                    log.info("停服等待数据落地 sql batch {}", Thread.currentThread());
                }
            }
            log.info("线程 {} 退出", Thread.currentThread());
        }

        private class BatchJob extends ExecutorEvent {

            @Override public void onEvent() throws Exception {
                BatchThread.this.insertBach();
                BatchThread.this.updateBach();
            }

        }

        protected void insertBach() {
            Table<String, String, ConvertCollection<BatchParam>> tmp;
            lock.lock();
            try {
                if (batchInsertMap.isEmpty()) {
                    return;
                }
                tmp = batchInsertMap;
                batchInsertMap = new Table<>();
            } finally {
                lock.unlock();
            }
            executeBach("insertBach", tmp);
        }

        protected void updateBach() {
            Table<String, String, ConvertCollection<BatchParam>> tmp;
            lock.lock();
            try {
                if (batchUpdateMap.isEmpty()) {
                    return;
                }
                tmp = batchUpdateMap;
                batchUpdateMap = new Table<>();
            } finally {
                lock.unlock();
            }
            executeBach("updateBach", tmp);
        }

        protected void executeBach(String sqlType, Table<String, String, ConvertCollection<BatchParam>> tmp) {
            for (HashMap<String, ConvertCollection<BatchParam>> map : tmp.values()) {
                for (Map.Entry<String, ConvertCollection<BatchParam>> entry : map.entrySet()) {
                    String sql = entry.getKey();
                    ConvertCollection<BatchParam> values = entry.getValue();
                    SplitCollection<BatchParam> splitCollection = new SplitCollection<>(batchSubmitSize, values.getNodes());
                    while (!splitCollection.isEmpty()) {
                        List<BatchParam> batchParams = splitCollection.removeFirst();
                        diffTime.reset();
                        int executeCount = executeUpdate(sql, batchParams);
                        long diff = diffTime.diffNs() / 10000;
                        executeDiffTime += diff;
                        this.executeCount += executeCount;
                        if (sqlDataHelper.getSqlConfig().isDebug() || ticket.need() || closed.get() || GlobalUtil.Exiting.get()) {
                            log.info(
                                    """
                                            
                                            %s 数据库: %s, %s(%s) , 单次批量提交数量限制: %s, 当前待提交剩余: %s 条
                                            本次 count: %s 条 耗时: %s ms 性能：%s 条/s
                                            累计 count: %s 条 耗时: %s ms 性能：%s 条/s
                                            """
                                            .formatted(
                                                    sqlDataHelper.getClass().getSimpleName(),
                                                    sqlDataHelper.getDbName(),
                                                    sqlType, sql,
                                                    batchSubmitSize, splitCollection.size(),
                                                    StringUtils.padLeft(executeCount, 19, ' '),
                                                    StringUtils.padLeft(diff / 100f, 19, ' '),
                                                    StringUtils.padLeft(executeCount / (diff / 100f) * 1000, 19, ' '),
                                                    StringUtils.padLeft(this.executeCount, 19, ' '),
                                                    StringUtils.padLeft(executeDiffTime / 100f, 19, ' '),
                                                    StringUtils.padLeft(this.executeCount / (executeDiffTime / 100f) * 1000, 19, ' ')
                                            )
                            );
                        }
                    }
                }
            }
        }

        protected int executeUpdate(String sql, List<BatchParam> paramList) {
            int insertCount = 0;
            try (Connection connection = sqlDataHelper.connection()) {
                connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    for (BatchParam objects : paramList) {
                        for (int i = 0; i < objects.params.length; i++) {
                            Object object = objects.params[i];
                            preparedStatement.setObject(i + 1, object);
                        }
                        preparedStatement.addBatch();
                        insertCount++;
                    }
                    preparedStatement.executeBatch();
                    connection.commit();
                }
            } catch (Exception e) {
                insertCount = 0;
                for (BatchParam objects : paramList) {
                    try (Connection connection = sqlDataHelper.connection();
                         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        for (int i = 0; i < objects.params.length; i++) {
                            Object object = objects.params[i];
                            preparedStatement.setObject(i + 1, object);
                        }
                        preparedStatement.executeUpdate();
                        insertCount++;
                    } catch (Exception e1) {
                        File file = new File("target/db_error/" + sqlDataHelper.getDbName() + "/" + this.threadId + "_" + System.nanoTime() + ".txt");
                        String formatted = "db batch error\nsql: %s\ndata: %s\n\nerror: \n%s".formatted(sql, FastJsonUtil.toJSONString(objects), e1.toString());
                        FileWriteUtil.writeString(file, formatted, true);
                        log.error("{}", formatted, e1);
                    }
                }
            }
            return insertCount;
        }
    }

    @Getter
    protected class BatchParam {

        protected final Entity entity;
        /** 主键的值 */
        protected final Object[] keyParams;
        protected final Object[] params;

        public BatchParam(Entity entity, Object[] keyParams, Object[] params) {
            this.entity = entity;
            this.keyParams = keyParams;
            this.params = params;
        }

        @Override public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            BatchParam that = (BatchParam) o;
            return getEntity().equals(that.getEntity());
        }

        @Override public int hashCode() {
            return getEntity().hashCode();
        }

    }

}
