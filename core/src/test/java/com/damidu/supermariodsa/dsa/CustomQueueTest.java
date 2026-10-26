package com.damidu.supermariodsa.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomQueueTest {

    private CustomQueue<Integer> queue;

    @BeforeEach
    void setUp() {
        queue = new CustomQueue<>(5);
    }

    @Test
    @DisplayName("Enqueue and dequeue strictly follow FIFO order")
    void testEnqueueAndDequeueFIFO() {
        assertTrue(queue.isEmpty());
        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        assertEquals(3, queue.size());
        assertEquals(10, queue.dequeue());
        assertEquals(20, queue.dequeue());
        assertEquals(30, queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    @DisplayName("Peek retrieves front element without removal")
    void testPeek() {
        queue.enqueue(99);
        assertEquals(99, queue.peek());
        assertEquals(1, queue.size());
    }

    @Test
    @DisplayName("Dequeue on empty queue returns null safely")
    void testEmptyHandling() {
        assertNull(queue.dequeue());
        assertNull(queue.peek());
    }

    @Test
    @DisplayName("Enqueue rejects elements when queue capacity is reached")
    void testCapacityRejection() {
        for (int i = 0; i < 5; i++) {
            assertTrue(queue.enqueue(i));
        }
        assertEquals(5, queue.size());
        // 6th item should be rejected
        assertFalse(queue.enqueue(999));
        assertEquals(5, queue.size());
    }
}
