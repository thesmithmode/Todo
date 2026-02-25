package com.rmk.todoapp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskFilterTest {

    @Test
    void testFromStringActive() {
        assertEquals(TaskFilter.ACTIVE, TaskFilter.fromString("active"));
        assertEquals(TaskFilter.ACTIVE, TaskFilter.fromString("ACTIVE"));
        assertEquals(TaskFilter.ACTIVE, TaskFilter.fromString("Active"));
    }

    @Test
    void testFromStringCompleted() {
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromString("completed"));
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromString("COMPLETED"));
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromString("Completed"));
    }

    @Test
    void testFromStringAll() {
        assertEquals(TaskFilter.ALL, TaskFilter.fromString("all"));
        assertEquals(TaskFilter.ALL, TaskFilter.fromString("ALL"));
        assertEquals(TaskFilter.ALL, TaskFilter.fromString("All"));
    }

    @Test
    void testFromStringNullReturnsAll() {
        assertEquals(TaskFilter.ALL, TaskFilter.fromString(null));
    }

    @Test
    void testFromStringInvalidReturnsAll() {
        assertEquals(TaskFilter.ALL, TaskFilter.fromString("invalid"));
        assertEquals(TaskFilter.ALL, TaskFilter.fromString("random"));
        assertEquals(TaskFilter.ALL, TaskFilter.fromString(""));
    }

    @Test
    void testGetValue() {
        assertEquals("active", TaskFilter.ACTIVE.getValue());
        assertEquals("completed", TaskFilter.COMPLETED.getValue());
        assertEquals("all", TaskFilter.ALL.getValue());
    }
}
