package com.rmk.todoapp.controllers;

import com.rmk.todoapp.model.TaskFilter;
import com.rmk.todoapp.model.TodoItem;
import com.rmk.todoapp.service.TodoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TodoController implements CommandLineRunner {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String filter,
            Model model
    ) {
        List<TodoItem> allTodos;
        TaskFilter taskFilter = TaskFilter.fromString(filter);

        if (search != null && !search.isBlank()) {
            allTodos = todoService.search(search);
            model.addAttribute("searchTerm", search);
        } else {
            allTodos = todoService.findByFilter(taskFilter);
            if (taskFilter != TaskFilter.ALL) {
                model.addAttribute("filter", taskFilter.getValue());
            }
        }

        long activeCount = todoService.countActive();
        long completedCount = todoService.countCompleted();

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
        if (todoItem.getTitle().length() > 255) {
            return "redirect:/";
        }
        todoService.create(todoItem.getTitle());
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable("id") Long id) {
        todoService.toggle(id);
        return "redirect:/";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, @RequestParam("title") String title) {
        if (title == null || title.isBlank() || title.length() > 255) {
            return "redirect:/";
        }
        todoService.updateTitle(id, title);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        todoService.delete(id);
        return "redirect:/";
    }

    @PostMapping("/removeAll")
    public String removeAll() {
        todoService.deleteAll();
        return "redirect:/";
    }

    @PostMapping("/clearCompleted")
    public String clearCompleted() {
        todoService.deleteCompleted();
        return "redirect:/";
    }

    @PostMapping("/search")
    public String search(@RequestParam("searchTerm") String searchTerm) throws Exception {
        return "redirect:/?search=" + java.net.URLEncoder.encode(searchTerm, "UTF-8");
    }

    @Override
    public void run(String... args) {
        if (todoService.count() == 0) {
            todoService.create("Изучить Spring Boot");
            todoService.create("Создать REST API");
            todoService.create("Написать тесты");
        }
    }
}
