package com.rmk.todoapp.repository;

import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.repositories.TodoItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TodoItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoItemRepository repository;

    private TodoItem item1;
    private TodoItem item2;
    private TodoItem item3;

    @BeforeEach
    void setUp() {
        item1 = new TodoItem("Купить продукты");
        item1.setCompleted(false);
        
        item2 = new TodoItem("Помыть машину");
        item2.setCompleted(true);
        
        item3 = new TodoItem("купить билеты");
        item3.setCompleted(false);

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();
    }

    @Test
    void testSaveAndFindById() {
        TodoItem newItem = new TodoItem("Новая задача");
        TodoItem saved = repository.save(newItem);

        assertNotNull(saved.getId());
        
        Optional<TodoItem> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Новая задача", found.get().getTitle());
    }

    @Test
    void testFindAll() {
        List<TodoItem> all = repository.findAll();
        assertEquals(3, all.size());
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        List<TodoItem> results = repository.findByTitleContainingIgnoreCase("купить");
        assertEquals(2, results.size());

        results = repository.findByTitleContainingIgnoreCase("КУПИТЬ");
        assertEquals(2, results.size());

        results = repository.findByTitleContainingIgnoreCase("продукты");
        assertEquals(1, results.size());

        results = repository.findByTitleContainingIgnoreCase("несуществующее");
        assertEquals(0, results.size());
    }

    @Test
    void testFindByCompleted() {
        List<TodoItem> completed = repository.findByCompleted(true);
        assertEquals(1, completed.size());
        assertEquals("Помыть машину", completed.get(0).getTitle());

        List<TodoItem> active = repository.findByCompleted(false);
        assertEquals(2, active.size());
    }

    @Test
    void testDeleteById() {
        Long id = item1.getId();
        repository.deleteById(id);

        Optional<TodoItem> deleted = repository.findById(id);
        assertFalse(deleted.isPresent());
    }

    @Test
    void testDeleteAll() {
        repository.deleteAll();
        List<TodoItem> all = repository.findAll();
        assertEquals(0, all.size());
    }

    @Test
    void testUpdate() {
        item1.setTitle("Обновлённая задача");
        item1.setCompleted(true);
        repository.save(item1);

        Optional<TodoItem> updated = repository.findById(item1.getId());
        assertTrue(updated.isPresent());
        assertEquals("Обновлённая задача", updated.get().getTitle());
        assertTrue(updated.get().isCompleted());
    }

    @Test
    void testCount() {
        long count = repository.count();
        assertEquals(3, count);
    }

    @Test
    void testCountByCompleted() {
        long activeCount = repository.countByCompleted(false);
        long completedCount = repository.countByCompleted(true);

        assertEquals(2, activeCount);
        assertEquals(1, completedCount);
    }
}
