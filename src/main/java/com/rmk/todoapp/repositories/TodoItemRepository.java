package com.rmk.todoapp.repositories;

import com.rmk.todoapp.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
}
