package cache;

public interface Cache<K, V> {
    public V getValue(K key);
    public void setValue(K key, V value);
}


