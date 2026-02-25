package com.rmk.todoapp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TodoItemTest {

    @Test
    void testConstructorWithTitle() {
        String title = "Тестовая задача";
        TodoItem item = new TodoItem(title);

        assertEquals(title, item.getTitle());
        assertFalse(item.isCompleted());
        assertNotNull(item.getCreatedAt());
    }

    @Test
    void testDefaultValues() {
        TodoItem item = new TodoItem();
        assertNull(item.getId());
        assertNull(item.getTitle());
        assertFalse(item.isCompleted());
        assertNull(item.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        TodoItem item = new TodoItem();
        Long id = 1L;
        String title = "Новая задача";
        boolean completed = true;
        LocalDateTime createdAt = LocalDateTime.now();

        item.setId(id);
        item.setTitle(title);
        item.setCompleted(completed);
        item.setCreatedAt(createdAt);

        assertEquals(id, item.getId());
        assertEquals(title, item.getTitle());
        assertEquals(completed, item.isCompleted());
        assertEquals(createdAt, item.getCreatedAt());
    }

    @Test
    void testSetCompleted() {
        TodoItem item = new TodoItem("Задача");

        assertFalse(item.isCompleted());

        item.setCompleted(true);
        assertTrue(item.isCompleted());

        item.setCompleted(false);
        assertFalse(item.isCompleted());
    }

    @Test
    void testTitleMaxLength() {
        TodoItem item = new TodoItem();
        String longTitle = "a".repeat(255);
        item.setTitle(longTitle);
        assertEquals(255, item.getTitle().length());
    }

    @Test
    void testTitleExceedsMaxLength() {
        TodoItem item = new TodoItem();
        String tooLongTitle = "a".repeat(256);
        item.setTitle(tooLongTitle);
        assertEquals(256, item.getTitle().length());
    }
}
