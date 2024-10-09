package wxdgaming.spring.boot.data.excel.code;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.spring.boot.core.ReflectContext;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.format.StreamWriter;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.ConvertUtil;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.core.system.AnnUtil;
import wxdgaming.spring.boot.core.system.FieldUtil;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据模型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-09 10:08
 **/
@Getter
public abstract class DataTable<E extends DataKey> extends ObjectBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;
    final Class<?> tClass;
    final Map<String, Field> fieldMap;
    private List<E> dataList = null;
    private Map<Object, E> dataMap = null;

    public DataTable() {
        tClass = ReflectContext.getTClass(this.getClass());
        fieldMap = FieldUtil.getFields(false, tClass);
    }

    public DataTable setModelList(List<E> modelList) {
        if (modelList != null) {
            /*不可变列表*/
            this.dataList = List.copyOf(modelList);
            this.dataMap = new LinkedHashMap<>();
            this.dataList.forEach((dbModel) -> {
                try {
                    Object keyValue = dbModel.key();
                    if (this.dataMap.put(keyValue, dbModel) != null) {
                        throw new RuntimeException("数据 主键 【" + keyValue + "】 重复");
                    }
                    Keys keys = AnnUtil.ann(DataTable.this.getClass(), Keys.class);
                    if (keys != null) {
                        for (String s : keys.value()) {
                            String index = "";
                            String[] split = s.split(keys.split());
                            for (String filedName : split) {
                                Object fv = fieldMap.get(filedName).get(dbModel);
                                if (!index.isEmpty()) index += keys.split();
                                index += fv;
                            }
                            /*添加自定义索引*/
                            if (this.dataMap.put(index, dbModel) != null) {
                                throw new Throw("数据 自定义索引 【" + s + "】 【" + keyValue + "】 重复 ");
                            }
                        }
                    }
                } catch (Throwable e) {
                    throw Throw.of("数据：" + FastJsonUtil.toJson(dbModel), e);
                }
            });
            /*不可变的列表*/
            this.dataMap = Map.copyOf(this.dataMap);
        }
        return this;
    }

    /**
     * 根据key值获取参数
     *
     * @param key
     * @return
     */
    public E get(Object key) {
        return dataMap.get(key);
    }

    public boolean containsKey(Object key) {
        return dataMap.containsKey(key);
    }

    @JSONField(serialize = false, deserialize = false)
    public int dbSize() {
        return this.dataList.size();
    }

    /** 初始化，做一些构建相关的操作 */
    @JSONField(serialize = false, deserialize = false)
    public void initDb() {
    }

    /** 检查数据合法性 */
    // @JSONField(serialize = false, deserialize = false)
    // public void checkDb(S dataRepository) {
    // }
    public String toDataString() {
        return toDataString(50);
    }

    public String toDataString(int len) {
        StreamWriter streamWriter = new StreamWriter();
        toDataString(streamWriter, len);
        return streamWriter.toString();
    }

    public void toDataString(StreamWriter streamWriter) {
        toDataString(streamWriter, 50);
    }

    public void toDataString(StreamWriter streamWriter, int len) {
        DataMapping dataMapping = AnnUtil.ann(tClass, DataMapping.class, true);
        streamWriter.write("解析：").write(tClass.getName()).write("\n");
        streamWriter.write("表名：").write(dataMapping.name()).write("\n");

        streamWriter.writeRight("-", len * fieldMap.size(), '-').write("\n");
        for (String columnName : fieldMap.keySet()) {
            streamWriter.write("|").writeRight(columnName, len, ' ');
        }

        streamWriter.write("\n");

        streamWriter.writeRight("-", len * fieldMap.size(), '-').write("\n");
        for (E row : dataList) {
            streamWriter.write("\n");
            for (Field entityField : fieldMap.values()) {
                Object value = null;
                try {
                    value = entityField.get(row);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (value == null) {
                    streamWriter.write("|").writeRight("-", len, ' ');
                } else if (ConvertUtil.isBaseType(value.getClass())) {
                    streamWriter.write("|").writeRight(String.valueOf(value), len, ' ');
                } else {
                    streamWriter.write("|").writeRight(FastJsonUtil.toJson(value), len, ' ');
                }
            }
        }
        streamWriter.write("\n");
    }

}
