package com.damidu.supermariodsa.dsa;

/**
 * Custom FIFO (First-In-First-Out) Queue implemented with singly-linked nodes.
 * Used for buffering game events (coin pickup, enemy stomp, damage, level notices)
 * to process them sequentially without blocking gameplay.
 *
 * Enqueue and dequeue both execute in O(1) time.
 *
 * @param <T> Element type
 */
public class CustomQueue<T> {

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;
    private final int maxCapacity;

    public CustomQueue(int maxCapacity) {
        this.front = null;
        this.rear = null;
        this.size = 0;
        this.maxCapacity = maxCapacity;
    }

    public CustomQueue() {
        this(50); // Default queue capacity
    }

    /**
     * Enqueues an item at the rear of the queue. O(1) time complexity.
     */
    public boolean enqueue(T item) {
        if (item == null) return false;
        if (size >= maxCapacity) {
            return false; // Queue full
        }
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
        return true;
    }

    /**
     * Dequeues and returns the item at the front of the queue. O(1) time complexity.
     * Safely returns null if the queue is empty.
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T data = front.data;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }

    /**
     * Inspects the front item without removing it. O(1) time complexity.
     */
    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return front.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }
}
