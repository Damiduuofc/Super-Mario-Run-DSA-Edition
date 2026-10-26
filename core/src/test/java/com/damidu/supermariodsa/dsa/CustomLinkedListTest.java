package com.damidu.supermariodsa.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomLinkedListTest {

    private CustomLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    @DisplayName("Adding elements updates size and preserves order")
    void testAddAndSize() {
        assertTrue(list.isEmpty());
        list.add("Alpha");
        list.add("Beta");
        list.add("Gamma");

        assertEquals(3, list.size());
        assertEquals("Alpha", list.get(0));
        assertEquals("Beta", list.get(1));
        assertEquals("Gamma", list.get(2));
    }

    @Test
    @DisplayName("addFirst and addLast manage head and tail correctly")
    void testAddFirstAndAddLast() {
        list.addLast("Middle");
        list.addFirst("Front");
        list.addLast("Back");

        assertEquals(3, list.size());
        assertEquals("Front", list.get(0));
        assertEquals("Middle", list.get(1));
        assertEquals("Back", list.get(2));
    }

    @Test
    @DisplayName("Removing by value correctly unlinks nodes")
    void testRemove() {
        list.add("A");
        list.add("B");
        list.add("C");

        assertTrue(list.remove("B"));
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1));
        assertFalse(list.remove("NonExistent"));
    }

    @Test
    @DisplayName("removeAt removes by index and handles boundaries")
    void testRemoveAt() {
        list.add("First");
        list.add("Second");
        list.add("Third");

        String removed = list.removeAt(0);
        assertEquals("First", removed);
        assertEquals(2, list.size());
        assertEquals("Second", list.get(0));

        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(10));
    }

    @Test
    @DisplayName("Iterable works with enhanced for-loop")
    void testIterator() {
        list.add("1");
        list.add("2");
        list.add("3");

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        assertEquals("123", sb.toString());
    }

    @Test
    @DisplayName("Contains checks presence accurately")
    void testContains() {
        list.add("Mario");
        list.add("Luigi");

        assertTrue(list.contains("Mario"));
        assertTrue(list.contains("Luigi"));
        assertFalse(list.contains("Bowser"));
    }
}
