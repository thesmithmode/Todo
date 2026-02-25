package com.rmk.todoapp.controller;

import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.repositories.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rmk.todoapp.controllers.TodoController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoItemRepository repository;

    private TodoItem item1;
    private TodoItem item2;
    private TodoItem item3;

    @BeforeEach
    void setUp() {
        item1 = new TodoItem("Активная задача");
        item1.setId(1L);
        item1.setCompleted(false);
        item1.setCreatedAt(LocalDateTime.now());

        item2 = new TodoItem("Выполненная задача");
        item2.setId(2L);
        item2.setCompleted(true);
        item2.setCreatedAt(LocalDateTime.now());

        item3 = new TodoItem("Вторая активная");
        item3.setId(3L);
        item3.setCompleted(false);
        item3.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testIndexReturnsPage() throws Exception {
        List<TodoItem> items = Arrays.asList(item1, item2, item3);
        when(repository.findAll()).thenReturn(items);
        when(repository.countByCompleted(false)).thenReturn(2L);
        when(repository.countByCompleted(true)).thenReturn(1L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("allTodos"))
                .andExpect(model().attribute("activeCount", 2L))
                .andExpect(model().attribute("completedCount", 1L));
    }

    @Test
    void testIndexWithEmptyList() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(repository.countByCompleted(false)).thenReturn(0L);
        when(repository.countByCompleted(true)).thenReturn(0L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("activeCount", 0L))
                .andExpect(model().attribute("completedCount", 0L));
    }

    @Test
    void testAddTask() throws Exception {
        when(repository.save(any(TodoItem.class))).thenReturn(item1);

        mockMvc.perform(post("/add")
                        .param("title", "Новая задача"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).save(any(TodoItem.class));
    }

    @Test
    void testAddEmptyTaskDoesNothing() throws Exception {
        mockMvc.perform(post("/add")
                        .param("title", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository, never()).save(any());
    }

    @Test
    void testAddNullTaskDoesNothing() throws Exception {
        mockMvc.perform(post("/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository, never()).save(any());
    }

    @Test
    void testAddWhitespaceOnlyTaskDoesNothing() throws Exception {
        mockMvc.perform(post("/add")
                        .param("title", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository, never()).save(argThat(item -> 
            item.getTitle() == null || item.getTitle().isBlank()
        ));
    }

    @Test
    void testAddTooLongTitleDoesNothing() throws Exception {
        String tooLongTitle = "a".repeat(256);
        mockMvc.perform(post("/add")
                        .param("title", tooLongTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository, never()).save(any());
    }

    @Test
    void testAddMaxLengthTitleSaves() throws Exception {
        String maxLengthTitle = "a".repeat(255);
        when(repository.save(any(TodoItem.class))).thenReturn(item1);

        mockMvc.perform(post("/add")
                        .param("title", maxLengthTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).save(any(TodoItem.class));
    }

    @Test
    void testToggleTask() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(item1));

        mockMvc.perform(post("/toggle/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).findById(1L);
        verify(repository).save(any(TodoItem.class));
    }

    @Test
    void testToggleNonExistentTask() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/toggle/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void testEditTask() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(item1));

        mockMvc.perform(post("/edit/1")
                        .param("title", "Обновлённая задача"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).findById(1L);
        verify(repository).save(any(TodoItem.class));
    }

    @Test
    void testEditTaskWithEmptyTitleDoesNothing() throws Exception {
        mockMvc.perform(post("/edit/1")
                        .param("title", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository, never()).save(any());
    }

    @Test
    void testEditTaskWithTooLongTitleDoesNothing() throws Exception {
        String tooLongTitle = "a".repeat(256);
        mockMvc.perform(post("/edit/1")
                        .param("title", tooLongTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository, never()).save(any());
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(post("/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).deleteById(1L);
    }

    @Test
    void testRemoveAll() throws Exception {
        mockMvc.perform(post("/removeAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).deleteAll();
    }

    @Test
    void testClearCompleted() throws Exception {
        when(repository.findByCompleted(true)).thenReturn(Arrays.asList(item2));

        mockMvc.perform(post("/clearCompleted"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(repository).findByCompleted(true);
        verify(repository).deleteAll(any(List.class));
    }

    @Test
    void testSearchTasks() throws Exception {
        List<TodoItem> searchResults = Arrays.asList(item1);
        when(repository.findByTitleContainingIgnoreCase("активная")).thenReturn(searchResults);
        when(repository.countByCompleted(false)).thenReturn(2L);
        when(repository.countByCompleted(true)).thenReturn(1L);

        mockMvc.perform(get("/").param("search", "активная"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("searchTerm", "активная"))
                .andExpect(model().attributeExists("allTodos"));

        verify(repository).findByTitleContainingIgnoreCase("активная");
    }

    @Test
    void testFilterActive() throws Exception {
        List<TodoItem> activeItems = Arrays.asList(item1, item3);
        when(repository.findByCompleted(false)).thenReturn(activeItems);
        when(repository.countByCompleted(false)).thenReturn(2L);
        when(repository.countByCompleted(true)).thenReturn(1L);

        mockMvc.perform(get("/").param("filter", "active"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("filter", "active"));

        verify(repository, atLeastOnce()).findByCompleted(false);
        verify(repository, never()).findAll();
    }

    @Test
    void testFilterCompleted() throws Exception {
        List<TodoItem> completedItems = Arrays.asList(item2);
        when(repository.findByCompleted(true)).thenReturn(completedItems);
        when(repository.countByCompleted(false)).thenReturn(2L);
        when(repository.countByCompleted(true)).thenReturn(1L);

        mockMvc.perform(get("/").param("filter", "completed"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("filter", "completed"));

        verify(repository, atLeastOnce()).findByCompleted(true);
    }

    @Test
    void testSearchRedirect() throws Exception {
        mockMvc.perform(post("/search")
                        .param("searchTerm", "тест"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?search=%D1%82%D0%B5%D1%81%D1%82"));
    }
}
