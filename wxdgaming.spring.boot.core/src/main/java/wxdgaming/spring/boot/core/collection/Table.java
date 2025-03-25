package wxdgaming.spring.boot.core.collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.format.data.Data2Json;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 切记，json序列化 请使用type类型
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-04-19 16:08
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Table<K1, K2, V> implements Serializable, Data2Json {

    private static final Map EMPTY_MAP = Map.of();

    private final HashMap<K1, HashMap<K2, V>> nodes;

    public Table() {
        this(16);
    }

    public Table(int initialCapacity) {
        nodes = new HashMap<>(initialCapacity);
    }

    public Table(Map<K1, Map<K2, V>> m) {
        this(m.size() + 10);
        for (Map.Entry<K1, Map<K2, V>> entry : m.entrySet()) {
            putAll(entry.getKey(), entry.getValue());
        }
    }

    public Table<K1, K2, V> putAll(K1 k1, Map<K2, V> m) {
        row(k1).putAll(m);
        return this;
    }

    public Table<K1, K2, V> append(K1 k1, K2 k2, V v) {
        row(k1).put(k2, v);
        return this;
    }

    public V put(K1 k1, K2 k2, V v) {
        return row(k1).put(k2, v);
    }

    /** 行 */
    public Map<K2, V> row(K1 k1) {
        return nodes.computeIfAbsent(k1, k -> new HashMap<>());
    }

    public V computeIfAbsent(K1 k1, K2 k2, Function<? super K2, ? extends V> mappingFunction) {
        return row(k1).computeIfAbsent(k2, mappingFunction);
    }

    /** 所有的value */
    public Collection<V> allValues() {
        Collection<V> collection = new ArrayList<>();
        for (Map<K2, V> value : nodes.values()) {
            collection.addAll(value.values());
        }
        return collection;
    }

    public Set<Map.Entry<K1, HashMap<K2, V>>> entrySet() {
        return this.nodes.entrySet();
    }

    public Set<K1> keySet() {
        return this.nodes.keySet();
    }

    public Collection<HashMap<K2, V>> values() {
        return this.nodes.values();
    }

    /** 循环 */
    public void forEach(Consumer<V> consumer) {
        nodes.values().forEach(v -> v.values().forEach(consumer));
    }

    /** 查询 */
    public V find(Predicate<V> predicate) {
        for (Map<K2, V> value : nodes.values()) {
            for (V v : value.values()) {
                if (predicate.test(v)) return v;
            }
        }
        return null;
    }

    /** 查询 */
    public V find(K2 k2) {
        for (Map<K2, V> value : nodes.values()) {
            final V v = value.get(k2);
            if (v != null) return v;
        }
        return null;
    }

    public Optional<Map<K2, V>> opt(K1 k) {
        return Optional.ofNullable(nodes.get(k));
    }

    public Optional<V> opt(K1 k1, K2 k2) {
        return opt(k1).map(v -> v.get(k2));
    }

    public Map<K2, V> get(K1 key) {
        return opt(key).orElse(EMPTY_MAP);
    }

    public V get(K1 k1, K2 k2) {
        return opt(k1, k2).orElse(null);
    }

    public Map<K2, V> remove(K1 k1) {
        return nodes.remove(k1);
    }

    public V remove(K1 k1, K2 k2) {
        V remove = null;
        Map<K2, V> k2VHashMap = nodes.get(k1);
        if (k2VHashMap != null) {
            remove = k2VHashMap.remove(k2);
            if (k2VHashMap.isEmpty()) {
                nodes.remove(k1);
            }
        }
        return remove;
    }

}
