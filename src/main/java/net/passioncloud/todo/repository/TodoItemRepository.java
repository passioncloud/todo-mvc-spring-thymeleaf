package net.passioncloud.todo.repository;

import net.passioncloud.todo.models.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    int countAllByCompleted(boolean completed);
    List<TodoItem> findAllByCompleted(boolean completed);
    boolean existsTodoItemByCompleted(boolean completed);
}
