package com.rmk.todoapp.controllers;

import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.repositories.TodoItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TodoController implements CommandLineRunner {

    private final TodoItemRepository todoItemRepository;

    public TodoController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filter,
            Model model
    ) {
        List<TodoItem> allTodos;

        if (search != null && !search.isBlank()) {
            allTodos = todoItemRepository.findByTitleContainingIgnoreCase(search);
            model.addAttribute("searchTerm", search);
        } else if ("active".equals(filter)) {
            allTodos = todoItemRepository.findByCompleted(false);
            model.addAttribute("filter", "active");
        } else if ("completed".equals(filter)) {
            allTodos = todoItemRepository.findByCompleted(true);
            model.addAttribute("filter", "completed");
        } else {
            allTodos = todoItemRepository.findAll();
        }

        long activeCount = todoItemRepository.findByCompleted(false).size();
        long completedCount = todoItemRepository.findByCompleted(true).size();

        model.addAttribute("allTodos", allTodos);
        model.addAttribute("newTodo", new TodoItem());
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);

        return "index";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute TodoItem todoItem) {
        if (todoItem.getTitle() == null || todoItem.getTitle().isBlank()) {
            return "redirect:/";
        }
        todoItemRepository.save(todoItem);
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable("id") Long id) {
        todoItemRepository.findById(id).ifPresent(item -> {
            item.setCompleted(!item.isCompleted());
            todoItemRepository.save(item);
        });
        return "redirect:/";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, @RequestParam("title") String title) {
        if (title == null || title.isBlank()) {
            return "redirect:/";
        }
        todoItemRepository.findById(id).ifPresent(item -> {
            item.setTitle(title);
            todoItemRepository.save(item);
        });
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        todoItemRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/removeAll")
    public String removeAll() {
        todoItemRepository.deleteAll();
        return "redirect:/";
    }

    @PostMapping("/clearCompleted")
    public String clearCompleted() {
        List<TodoItem> completed = todoItemRepository.findByCompleted(true);
        todoItemRepository.deleteAll(completed);
        return "redirect:/";
    }

    @PostMapping("/search")
    public String search(@RequestParam("searchTerm") String searchTerm) throws Exception {
        return "redirect:/?search=" + java.net.URLEncoder.encode(searchTerm, "UTF-8");
    }

    @Override
    public void run(String... args) {
        if (todoItemRepository.count() == 0) {
            todoItemRepository.save(new TodoItem("Изучить Spring Boot"));
            todoItemRepository.save(new TodoItem("Создать REST API"));
            todoItemRepository.save(new TodoItem("Написать тесты"));
        }
    }
}
