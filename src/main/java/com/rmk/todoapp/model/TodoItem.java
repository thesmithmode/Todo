package com.rmk.todoapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    private String title;

    public TodoItem(String title) {
        this.title = title;
    }
}
