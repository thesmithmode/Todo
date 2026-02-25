package com.rmk.todoapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TodoItem {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Название задачи не может быть пустым")
    @Size(max = 255, message = "Название задачи не может превышать 255 символов")
    private String title;

    private boolean completed = false;
    private java.time.LocalDateTime createdAt;

    public TodoItem(String title) {
        this.title = title;
        this.createdAt = java.time.LocalDateTime.now();
    }
}
