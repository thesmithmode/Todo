package com.rmk.todoapp.service;

import com.rmk.todoapp.model.TaskFilter;
import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.repositories.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoItemRepository repository;

    @InjectMocks
    private TodoService todoService;

    private TodoItem item1;
    private TodoItem item2;
    private TodoItem item3;

    @BeforeEach
    void setUp() {
        item1 = new TodoItem("Активная задача");
        item1.setId(1L);
        item1.setCompleted(false);

        item2 = new TodoItem("Выполненная задача");
        item2.setId(2L);
        item2.setCompleted(true);

        item3 = new TodoItem("Вторая активная");
        item3.setId(3L);
        item3.setCompleted(false);
    }

    @Test
    void testFindByFilterAll() {
        List<TodoItem> items = Arrays.asList(item1, item2, item3);
        when(repository.findAll()).thenReturn(items);

        List<TodoItem> result = todoService.findByFilter(TaskFilter.ALL);

        assertEquals(3, result.size());
        verify(repository).findAll();
    }

    @Test
    void testFindByFilterActive() {
        List<TodoItem> activeItems = Arrays.asList(item1, item3);
        when(repository.findByCompleted(false)).thenReturn(activeItems);

        List<TodoItem> result = todoService.findByFilter(TaskFilter.ACTIVE);

        assertEquals(2, result.size());
        verify(repository).findByCompleted(false);
    }

    @Test
    void testFindByFilterCompleted() {
        List<TodoItem> completedItems = Arrays.asList(item2);
        when(repository.findByCompleted(true)).thenReturn(completedItems);

        List<TodoItem> result = todoService.findByFilter(TaskFilter.COMPLETED);

        assertEquals(1, result.size());
        verify(repository).findByCompleted(true);
    }

    @Test
    void testSearch() {
        List<TodoItem> searchResults = Arrays.asList(item1);
        when(repository.findByTitleContainingIgnoreCase("активная")).thenReturn(searchResults);

        List<TodoItem> result = todoService.search("активная");

        assertEquals(1, result.size());
        verify(repository).findByTitleContainingIgnoreCase("активная");
    }

    @Test
    void testCountActive() {
        when(repository.countByCompleted(false)).thenReturn(2L);

        long count = todoService.countActive();

        assertEquals(2L, count);
        verify(repository).countByCompleted(false);
    }

    @Test
    void testCountCompleted() {
        when(repository.countByCompleted(true)).thenReturn(1L);

        long count = todoService.countCompleted();

        assertEquals(1L, count);
        verify(repository).countByCompleted(true);
    }

    @Test
    void testCreate() {
        when(repository.save(any(TodoItem.class))).thenAnswer(invocation -> {
            TodoItem saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        TodoItem result = todoService.create("Новая задача");

        assertEquals("Новая задача", result.getTitle());
        assertNotNull(result.getCreatedAt());
        verify(repository).save(any(TodoItem.class));
    }

    @Test
    void testToggle() {
        when(repository.findById(1L)).thenReturn(Optional.of(item1));
        when(repository.save(any(TodoItem.class))).thenReturn(item1);

        todoService.toggle(1L);

        assertTrue(item1.isCompleted());
        verify(repository).findById(1L);
        verify(repository).save(item1);
    }

    @Test
    void testToggleNonExistent() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        todoService.toggle(999L);

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void testUpdateTitle() {
        when(repository.findById(1L)).thenReturn(Optional.of(item1));
        when(repository.save(any(TodoItem.class))).thenReturn(item1);

        todoService.updateTitle(1L, "Обновлённая задача");

        assertEquals("Обновлённая задача", item1.getTitle());
        verify(repository).findById(1L);
        verify(repository).save(item1);
    }

    @Test
    void testUpdateTitleNonExistent() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        todoService.updateTitle(999L, "Новый заголовок");

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void testDelete() {
        todoService.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteAll() {
        todoService.deleteAll();

        verify(repository).deleteAll();
    }

    @Test
    void testDeleteCompleted() {
        List<TodoItem> completedItems = Arrays.asList(item2);
        when(repository.findByCompleted(true)).thenReturn(completedItems);

        todoService.deleteCompleted();

        verify(repository).findByCompleted(true);
        verify(repository).deleteAll(completedItems);
    }

    @Test
    void testCount() {
        when(repository.count()).thenReturn(3L);

        long count = todoService.count();

        assertEquals(3L, count);
        verify(repository).count();
    }

    @Test
    void testSave() {
        when(repository.save(any(TodoItem.class))).thenReturn(item1);

        todoService.save(item1);

        verify(repository).save(item1);
    }
}
