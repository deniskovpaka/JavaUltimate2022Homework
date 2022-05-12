package com.bobocode;

public class HashTable<T> {
    private Integer hashTableSize;

    private Node<T>[] nodes;

    @SuppressWarnings("unchecked")
    public HashTable() {
        this.hashTableSize = 4;
        nodes = new Node[hashTableSize];
    }


    /**
     * Adds an element to the hash table. Does not support duplicate elements.
     *
     * @param element
     * @return true if it was added
     */
    public boolean add(T element) {
        int nodeNumber = Math.abs(element.hashCode()) % hashTableSize;
        Node<T> existingNodes = nodes[nodeNumber];
        if (existingNodes == null) {
            nodes[nodeNumber] = new Node<>(element);
            return true;
        }
        while (existingNodes.next != null) {
            if (existingNodes.equals(element)) {
                return false;
            }
            existingNodes = existingNodes.next;
        }
        existingNodes.next = new Node<>(element);
        return true;
    }

    /**
     * Prints a hash table according to the following format
     * 0: Andrii -> Taras
     * 1: Start
     * 2: Serhii
     * ...
     */
    public void printTable() {
        for (int i = 0; i < nodes.length; ++i) {
            System.out.print(i + ": ");
            Node<T> node = nodes[i];
            while (node != null) {
                System.out.print(node.element + " ");
                node = node.next;
            }
            System.out.println();
        }
    }

    /**
     * Creates a new underlying table with a given size and add all elements to the new table.
     *
     * @param newSize
     */
    @SuppressWarnings("unchecked")
    public void resize(int newSize) {
        if (newSize <= nodes.length) {
            return;
        }
        hashTableSize = newSize;
        Node<T>[] newNodes = new Node[hashTableSize];
        System.arraycopy(nodes, 0, newNodes, 0, nodes.length);
        nodes = newNodes;
    }
}
