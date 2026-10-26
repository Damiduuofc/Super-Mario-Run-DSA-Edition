package com.damidu.supermariodsa.dsa;

/**
 * Custom LIFO (Last-In-First-Out) Stack with fixed capacity limit.
 * Used for storing player movement history snapshots for the Undo mechanic.
 *
 * All primary operations (push, pop, peek) operate in O(1) constant time.
 *
 * @param <T> Element type
 */
public class CustomStack<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;
    private final int maxCapacity;

    public CustomStack(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Stack capacity must be positive");
        }
        this.maxCapacity = maxCapacity;
        this.top = null;
        this.size = 0;
    }

    public CustomStack() {
        this(100); // Default to 100 snapshots
    }

    /**
     * Pushes an item onto the top of the stack. O(1) time complexity.
     * If capacity is reached, discards the oldest element (bottom of stack)
     * so that recent history is always preserved.
     */
    public void push(T item) {
        if (item == null) return;

        if (size >= maxCapacity) {
            trimBottom();
        }

        top = new Node<>(item, top);
        size++;
    }

    /**
     * Pops and returns the top item from the stack. O(1) time complexity.
     * Returns null if the stack is empty (safe handling).
     */
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }

    /**
     * Returns the top item without removing it. O(1) time complexity.
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return top.data;
    }

    /**
     * Removes the bottom-most (oldest) node when capacity is full.
     */
    private void trimBottom() {
        if (top == null || top.next == null) {
            top = null;
            size = 0;
            return;
        }
        Node<T> current = top;
        while (current.next != null && current.next.next != null) {
            current = current.next;
        }
        current.next = null;
        size--;
    }

    public int size() {
        return size;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size >= maxCapacity;
    }

    public void clear() {
        top = null;
        size = 0;
    }
}
