package com.damidu.supermariodsa.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomStackTest {

    private CustomStack<String> stack;

    @BeforeEach
    void setUp() {
        stack = new CustomStack<>(3); // Small capacity to test bounds
    }

    @Test
    @DisplayName("Push and pop follow LIFO order")
    void testPushAndPopLIFO() {
        stack.push("A");
        stack.push("B");
        stack.push("C");

        assertEquals(3, stack.size());
        assertEquals("C", stack.pop());
        assertEquals("B", stack.pop());
        assertEquals("A", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("Peek returns top item without removing it")
    void testPeek() {
        stack.push("Test");
        assertEquals("Test", stack.peek());
        assertEquals(1, stack.size());
    }

    @Test
    @DisplayName("Pop and peek on empty stack safely return null")
    void testEmptyHandling() {
        assertTrue(stack.isEmpty());
        assertNull(stack.pop());
        assertNull(stack.peek());
    }

    @Test
    @DisplayName("Stack enforces capacity limit by discarding oldest bottom elements")
    void testCapacityLimitAndBottomTrimming() {
        stack.push("Oldest");
        stack.push("Middle");
        stack.push("Recent");
        // Capacity reached: size == 3
        assertEquals(3, stack.size());

        // Pushing a 4th item trims "Oldest" from bottom
        stack.push("Newest");
        assertEquals(3, stack.size());

        assertEquals("Newest", stack.pop());
        assertEquals("Recent", stack.pop());
        assertEquals("Middle", stack.pop());
        assertNull(stack.pop());
    }
}
