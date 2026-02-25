package com.rmk.todoapp.controller;

import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rmk.todoapp.controllers.TodoController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    private TodoItem item1;
    private TodoItem item2;
    private TodoItem item3;

    @BeforeEach
    void setUp() {
        item1 = new TodoItem("Активная задача");
        item1.setId(1L);
        item1.setCompleted(false);
        item1.setCreatedAt(java.time.LocalDateTime.now());

        item2 = new TodoItem("Выполненная задача");
        item2.setId(2L);
        item2.setCompleted(true);
        item2.setCreatedAt(java.time.LocalDateTime.now());

        item3 = new TodoItem("Вторая активная");
        item3.setId(3L);
        item3.setCompleted(false);
        item3.setCreatedAt(java.time.LocalDateTime.now());
    }

    @Test
    void testIndexReturnsPage() throws Exception {
        List<TodoItem> items = Arrays.asList(item1, item2, item3);
        when(todoService.findByFilter(any())).thenReturn(items);
        when(todoService.countActive()).thenReturn(2L);
        when(todoService.countCompleted()).thenReturn(1L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("allTodos"))
                .andExpect(model().attribute("activeCount", 2L))
                .andExpect(model().attribute("completedCount", 1L));
    }

    @Test
    void testIndexWithEmptyList() throws Exception {
        when(todoService.findByFilter(any())).thenReturn(Collections.emptyList());
        when(todoService.countActive()).thenReturn(0L);
        when(todoService.countCompleted()).thenReturn(0L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("activeCount", 0L))
                .andExpect(model().attribute("completedCount", 0L));
    }

    @Test
    void testAddTask() throws Exception {
        mockMvc.perform(post("/add")
                        .param("title", "Новая задача"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).create("Новая задача");
    }

    @Test
    void testAddEmptyTaskDoesNothing() throws Exception {
        mockMvc.perform(post("/add")
                        .param("title", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService, never()).create(anyString());
    }

    @Test
    void testAddNullTaskDoesNothing() throws Exception {
        mockMvc.perform(post("/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService, never()).create(anyString());
    }

    @Test
    void testAddWhitespaceOnlyTaskDoesNothing() throws Exception {
        mockMvc.perform(post("/add")
                        .param("title", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService, never()).create(anyString());
    }

    @Test
    void testAddTooLongTitleDoesNothing() throws Exception {
        String tooLongTitle = "a".repeat(256);
        mockMvc.perform(post("/add")
                        .param("title", tooLongTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService, never()).create(anyString());
    }

    @Test
    void testAddMaxLengthTitleSaves() throws Exception {
        String maxLengthTitle = "a".repeat(255);

        mockMvc.perform(post("/add")
                        .param("title", maxLengthTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).create(maxLengthTitle);
    }

    @Test
    void testToggleTask() throws Exception {
        mockMvc.perform(post("/toggle/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).toggle(1L);
    }

    @Test
    void testEditTask() throws Exception {
        mockMvc.perform(post("/edit/1")
                        .param("title", "Обновлённая задача"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).updateTitle(1L, "Обновлённая задача");
    }

    @Test
    void testEditTaskWithEmptyTitleDoesNothing() throws Exception {
        mockMvc.perform(post("/edit/1")
                        .param("title", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService, never()).updateTitle(any(), anyString());
    }

    @Test
    void testEditTaskWithTooLongTitleDoesNothing() throws Exception {
        String tooLongTitle = "a".repeat(256);
        mockMvc.perform(post("/edit/1")
                        .param("title", tooLongTitle))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService, never()).updateTitle(any(), anyString());
    }

    @Test
    void testDeleteTask() throws Exception {
        mockMvc.perform(post("/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).delete(1L);
    }

    @Test
    void testRemoveAll() throws Exception {
        mockMvc.perform(post("/removeAll"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).deleteAll();
    }

    @Test
    void testClearCompleted() throws Exception {
        mockMvc.perform(post("/clearCompleted"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(todoService).deleteCompleted();
    }

    @Test
    void testSearchTasks() throws Exception {
        List<TodoItem> searchResults = Arrays.asList(item1);
        when(todoService.search("активная")).thenReturn(searchResults);
        when(todoService.countActive()).thenReturn(2L);
        when(todoService.countCompleted()).thenReturn(1L);

        mockMvc.perform(get("/").param("search", "активная"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("searchTerm", "активная"))
                .andExpect(model().attributeExists("allTodos"));

        verify(todoService).search("активная");
    }

    @Test
    void testFilterActive() throws Exception {
        List<TodoItem> activeItems = Arrays.asList(item1, item3);
        when(todoService.findByFilter(any())).thenReturn(activeItems);
        when(todoService.countActive()).thenReturn(2L);
        when(todoService.countCompleted()).thenReturn(1L);

        mockMvc.perform(get("/").param("filter", "active"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("filter", "active"));

        verify(todoService).findByFilter(any());
    }

    @Test
    void testFilterCompleted() throws Exception {
        List<TodoItem> completedItems = Arrays.asList(item2);
        when(todoService.findByFilter(any())).thenReturn(completedItems);
        when(todoService.countActive()).thenReturn(2L);
        when(todoService.countCompleted()).thenReturn(1L);

        mockMvc.perform(get("/").param("filter", "completed"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("filter", "completed"));

        verify(todoService).findByFilter(any());
    }

    @Test
    void testSearchRedirect() throws Exception {
        mockMvc.perform(post("/search")
                        .param("searchTerm", "тест"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?search=%D1%82%D0%B5%D1%81%D1%82"));
    }
}
