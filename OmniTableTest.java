
package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OmniTableTest {

    private OmniTable<Integer, String> table;

    @BeforeEach
    void setUp() {
        table = new OmniTable<>();
    }

    @Test
    void testInsertionAndLookup() {
        table.put(1, "One");
        table.put(2, "Two");
        table.put(3, "Three");

        assertEquals("One", table.get(1));
        assertEquals("Two", table.get(2));
        assertEquals("Three", table.get(3));
    }

    @Test
    void testDeletion() {
        table.put(1, "One");
        table.put(2, "Two");

        table.delete(1);
        assertNull(table.get(1));
        assertEquals("Two", table.get(2));
    }

    @Test
    void testOrderPreservation() {
        table.put(1, "One");
        table.put(2, "Two");
        table.put(3, "Three");

        assertEquals("[One, Two, Three]", table.getOrderedValues().toString());
    }

    @Test
    void testCollisionHandling() {
        // Force a collision by using keys that will hash to the same index
        table.put(1, "One");
        table.put(17, "Seventeen"); // Assuming table size is 16, this will cause a collision

        assertEquals("One", table.get(1));
        assertEquals("Seventeen", table.get(17));
    }

    @Test
    void testAdaptiveResizing() {
        for (int i = 0; i < 100; i++) {
            table.put(i, "Value" + i);
        }

        for (int i = 0; i < 100; i++) {
            assertEquals("Value" + i, table.get(i));
        }
        assertEquals(100, table.size());
    }
}
