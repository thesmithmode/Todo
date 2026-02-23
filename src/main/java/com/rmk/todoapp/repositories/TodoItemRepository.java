package com.rmk.todoapp.repositories;

import com.rmk.todoapp.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByTitleContainingIgnoreCase(String title);
    List<TodoItem> findByCompleted(boolean completed);
}
