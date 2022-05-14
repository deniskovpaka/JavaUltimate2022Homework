package com.bobocode;

import java.util.Objects;

/**
 * A simple implementation of the Hash Table that allows storing a generic key-value pair. The table itself is based
 * on the array of {@link Node} objects.
 * <p>
 * An initial array capacity is 16.
 * <p>
 * Every time a number of elements is equal to the array size that tables gets resized
 * (it gets replaced with a new array that it twice bigger than before). E.g. resize operation will replace array
 * of size 16 with a new array of size 32. PLEASE NOTE that all elements should be reinserted to the new table to make
 * sure that they are still accessible  from the outside by the same key.
 *
 * @param <K> key type parameter
 * @param <V> value type parameter
 */
public class HashTable<K, V> {
    private static final Integer INITIAL_CAPACITY = 16;
    private static final Integer RESIZE_MULTIPLIER = 2;
    private int currentBucketSize;

    @SuppressWarnings("unchecked")
    private Node<K, V>[] nodes = new Node[INITIAL_CAPACITY];

    /**
     * Puts a new element to the table by its key. If there is an existing element by such key then it gets replaced
     * with a new one, and the old value is returned from the method. If there is no such key then it gets added and
     * null value is returned.
     *
     * @param key   element key
     * @param value element value
     * @return old value or null
     */
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        int nodeBucket = hash(key) % nodes.length;
        if (nodes[nodeBucket] == null) {
            createNewBucket(nodeBucket, key, value);
            return null;
        } else {
            return addNewBucket(nodeBucket, key, value);
        }
    }

    private int hash(K key) {
        return Math.abs(key.hashCode());
    }

    private void createNewBucket(int nodeBucket, K key, V value) {
        nodes[nodeBucket] = new Node<>(key, value);
        ++currentBucketSize;
        if (currentBucketSize == nodes.length) {
            resize();
        }
    }

    private V addNewBucket(int nodeBucket, K key, V value) {
        Node<K, V> existingBucket = nodes[nodeBucket];
        while (existingBucket.next != null) {
            if (existingBucket.value.equals(value)) {
                V oldValue = existingBucket.value;
                existingBucket.value = value;
                return oldValue;
            }
        }
        createNewBucket(nodeBucket, key, value);
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] newNodes = new Node[nodes.length * RESIZE_MULTIPLIER];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        nodes = newNodes;
    }

    /**
     * Prints a content of the underlying table (array) according to the following format:
     * 0: key1:value1 -> key2:value2
     * 1:
     * 2: key3:value3
     * ...
     */
    public void printTable() {
        for (int i = 0; i < nodes.length; ++i) {
            System.out.print(i + ": ");
            Node<K, V> node = nodes[i];
            while (node != null) {
                System.out.print(node.key + ":" + node.value + " ");
                node = node.next;
            }
            System.out.println();
        }
    }
}
