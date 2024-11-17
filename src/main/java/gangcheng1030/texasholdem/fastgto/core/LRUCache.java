package gangcheng1030.texasholdem.fastgto.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    // 调用LinkedHashMap的构造函数
    public LRUCache(int capacity) {
        // 设置一个适当的负载因子以减少rehash操作
        // 第三个参数true表明LinkedHashMap按照访问顺序来排序，最近访问的在头部，最老访问的在尾部
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 当Map中数据量大于指定缓存个数的时候，返回true，自动删除最老的数据
        return size() > capacity;
    }

    // 提供get和put方法
    public V get(Object key) {
        return super.get(key);
    }

    public V put(K key, V value) {
        return super.put(key, value);
    }
}
