package com.rmk.todoapp.service;

import com.rmk.todoapp.model.TaskFilter;
import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.repositories.TodoItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TodoService {

    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TodoItem> findByFilter(TaskFilter filter) {
        return switch (filter) {
            case ACTIVE -> repository.findByCompleted(false);
            case COMPLETED -> repository.findByCompleted(true);
            case ALL -> repository.findAll();
        };
    }

    @Transactional(readOnly = true)
    public List<TodoItem> search(String query) {
        return repository.findByTitleContainingIgnoreCase(query);
    }

    @Transactional(readOnly = true)
    public long countActive() {
        return repository.countByCompleted(false);
    }

    @Transactional(readOnly = true)
    public long countCompleted() {
        return repository.countByCompleted(true);
    }

    public TodoItem create(String title) {
        TodoItem item = new TodoItem(title);
        return repository.save(item);
    }

    public void toggle(Long id) {
        repository.findById(id).ifPresent(item -> {
            item.setCompleted(!item.isCompleted());
            repository.save(item);
        });
    }

    public void updateTitle(Long id, String title) {
        repository.findById(id).ifPresent(item -> {
            item.setTitle(title);
            repository.save(item);
        });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void deleteCompleted() {
        List<TodoItem> completed = repository.findByCompleted(true);
        repository.deleteAll(completed);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    public void save(TodoItem item) {
        repository.save(item);
    }
}
