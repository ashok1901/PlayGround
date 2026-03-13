package cache.lru;

public class Node<K, V> {
    private V value;
    private K key;
    public Node(K key, V val) {
        this.key = key;
        this.value = val;
    }

    public V getValue() {
        return this.value;
    }

    public K getKey() {
        return this.key;
    }

    public void setValue(V value) {
        this.value = value;
    }
}