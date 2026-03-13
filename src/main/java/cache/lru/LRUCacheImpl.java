package cache.lru;

import cache.Cache;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class LRUCacheImpl<K, V> implements Cache<K, V> {
    // Eviction based on fixed size.
    private long capacity;
    private Map<K, Node<K, V>> valueNodeMap = new HashMap<>();
    private LinkedList<Node<K, V>> linkedList = new LinkedList<>();

    public LRUCacheImpl(long capacity) {
        this.capacity = capacity;
    }

    @Override
    public V getValue(K key) {
        System.out.println(String.format("Getting value for key %s", key));
        Node<K, V> valueNode = valueNodeMap.get(key);
        if (valueNode == null) {
            // Cache miss
            return null;
        }
        // Cache hit
        // Move it to the front
        linkedList.remove(valueNode);
        linkedList.addFirst(valueNode);
        return valueNode.getValue();
    }

    @Override
    public void setValue(K key, V value) {
        System.out.println(String.format("Setting value <%s, %s>", key, value));
        // Update the hashmap
        if (!valueNodeMap.containsKey(key)) {
            // First time setting this value
            // TODO: check the size and think about eviction
            evictNodeIfNeeded();
            Node<K, V> node = new Node<>(key, value);
            valueNodeMap.put(key, node);
            linkedList.addFirst(node);
        }
        // Node already exist so we need to move node it to the first.
        Node<K, V> node = valueNodeMap.get(key);
        // Change the value of the key
        node.setValue(value);
        // Pull the node to the front.
        linkedList.remove(node);
        linkedList.addFirst(node);
    }

    private void evictNodeIfNeeded() {
        int currentSize = valueNodeMap.size();
        while (currentSize >= capacity) {
            Node<K, V> lastNode = linkedList.getLast();
            linkedList.removeLast();
            valueNodeMap.remove(lastNode.getKey());
            System.out.println(String.format("Evicting node <%s, %s>", lastNode.getKey(), lastNode.getValue()));
            currentSize = valueNodeMap.size();
        }
    }

    public void printLinkedList() {

        String toStr = "[ ";
        for (Node<K, V> node : linkedList.stream().collect(Collectors.toList())) {
            toStr = toStr + String.format(" <%s, %s>", node.getKey(), node.getValue());
        }
        toStr = toStr + " ]";
        System.out.println("Linked list is " + toStr);
    }
    public static void main(String[] args) {
        LRUCacheImpl<String, String> cache = new LRUCacheImpl<>(5);
        cache.setValue("a1", "a1_value");
        cache.setValue("a2", "a2_value");
        cache.setValue("a3", "a3_value");
        cache.setValue("a4", "a4_value");
        cache.setValue("a5", "a5_value");
        String val = cache.getValue("a1");
        System.out.println("Value from cache : " + val);
        cache.setValue("a6", "a6_value");

        // Print the LinkedList
        cache.printLinkedList();
    }
}


