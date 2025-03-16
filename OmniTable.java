import java.util.*;
import net.openhft.hashing.LongHashFunction;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class OmniTable<K, V> {
    private static final int INITIAL_CAPACITY = 64;
    private static final float LOAD_FACTOR = 0.75f;
    private static final float RESIZE_THRESHOLD = 0.85f;
    private static final int CACHE_LINE_SIZE = 64;

    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private Node<K, V> head, tail;
    private static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        Node<K, V> prev;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @SuppressWarnings("unchecked")
    public OmniTable() {
        int alignedCapacity = alignToCacheLine(INITIAL_CAPACITY);
        table = (Node<K, V>[]) java.lang.reflect.Array.newInstance(Node.class, alignedCapacity);
        threshold = (int) (alignedCapacity * LOAD_FACTOR);
        size = 0;
    }

    private int alignToCacheLine(int capacity) {
        return (capacity + CACHE_LINE_SIZE - 1) & ~(CACHE_LINE_SIZE - 1);
    }

    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        long hash = LongHashFunction.xx().hashInt(key.hashCode());
        return (int) (hash & (table.length - 1));
    }

    public void put(K key, V value) {
        int index = hash(key);
        Node<K, V> newNode = new Node<>(key, value);

        // Direct Bucket Injection (DBI)
        if (table[index] == null) {
            table[index] = newNode;
            linkLast(newNode);
        } else {
            Node<K, V> current = table[index];
            int probeDistance = 0;

            while (current != null) {
                int currentDistance = getProbeDistance(hash(current.key), index);

                // Robin Hood fallback if DBI fails due to clustering
                if (probeDistance > currentDistance) {
                    Node<K, V> temp = current;
                    table[index] = newNode;
                    newNode.next = temp.next;
                    linkLast(newNode);
                    newNode = temp;
                    probeDistance = currentDistance;
                }

                if (Objects.equals(current.key, key)) {
                    current.value = value;
                    return;
                }

                probeDistance++;
                if (current.next == null) {
                    current.next = newNode;
                    linkLast(newNode);
                    break;
                }
                current = current.next;
            }
        }

        size++;
        if (needsResize()) {
            resize();
        }
    }

    private int getProbeDistance(int hash, int index) {
        return (index - hash + table.length) % table.length;
    }

    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public void delete(K key) {
        int index = hash(key);
        Node<K, V> current = table[index];
        Node<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                unlink(current);
                size--;
                return;
            }
            prev = current;
            current = current.next;
        }
    }

    private void linkLast(Node<K, V> node) {
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private void unlink(Node<K, V> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        if (node == head) {
            head = node.next;
        }
        if (node == tail) {
            tail = node.prev;
        }
    }

    private boolean needsResize() {
        float loadFactor = (float) size / table.length;
        return loadFactor > RESIZE_THRESHOLD;
    }

    // Adaptive Resizing: Adjust capacity based on sustained load factor
    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = alignToCacheLine((int) (table.length * 1.5));
        table = (Node<K, V>[]) java.lang.reflect.Array.newInstance(Node.class, newCapacity);
        threshold = (int) (newCapacity * LOAD_FACTOR);
        size = 0;

        Node<K, V> current = head;
        while (current != null) {
            put(current.key, current.value);
            current = current.next;
        }
    }

    public List<V> getOrderedValues() {
        List<V> values = new ArrayList<>();
        Node<K, V> current = head;
        while (current != null) {
            values.add(current.value);
            current = current.next;
        }
        return values;
    }

    public int size() {
        return size;
    }
}
