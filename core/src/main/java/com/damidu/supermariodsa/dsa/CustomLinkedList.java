package com.damidu.supermariodsa.dsa;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom Doubly Linked List implementation for university DSA evaluation.
 * Used for dynamic collections such as active game events, particles, and inventory items.
 *
 * @param <T> Element type
 */
public class CustomLinkedList<T> implements Iterable<T> {

    public static class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Inserts an element at the end of the list. O(1) time complexity.
     */
    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /**
     * Inserts an element at the front of the list. O(1) time complexity.
     */
    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    /**
     * Standard add appends to the tail. O(1) time complexity.
     */
    public void add(T data) {
        addLast(data);
    }

    /**
     * Removes and returns the first element. O(1) time complexity.
     */
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T data = head.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    /**
     * Removes and returns the last element. O(1) time complexity due to prev pointer.
     */
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T data = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    /**
     * Removes the first occurrence of data. O(n) search time, O(1) pointer unlinking.
     */
    public boolean remove(T data) {
        if (isEmpty() || data == null) {
            return false;
        }
        Node<T> current = head;
        while (current != null) {
            if (data.equals(current.data)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Removes the node at the specified index. O(n) traversal.
     */
    public T removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + size);
        }
        Node<T> current = getNode(index);
        unlink(current);
        return current.data;
    }

    private void unlink(Node<T> node) {
        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = head.next;
            head.prev = null;
        } else if (node == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        size--;
    }

    /**
     * Retrieves element at specified index. O(n) traversal optimized by starting from closest end.
     */
    public T get(int index) {
        return getNode(index).data;
    }

    private Node<T> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    public boolean contains(T data) {
        if (data == null) return false;
        Node<T> current = head;
        while (current != null) {
            if (data.equals(current.data)) return true;
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    public Node<T> getHead() {
        return head;
    }

    public Node<T> getTail() {
        return tail;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
}
