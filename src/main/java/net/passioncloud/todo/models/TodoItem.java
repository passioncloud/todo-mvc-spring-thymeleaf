package net.passioncloud.todo.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class TodoItem {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String title;

    private boolean completed;
}
