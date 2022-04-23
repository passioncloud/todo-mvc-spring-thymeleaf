package net.passioncloud.todo.controller;

import net.passioncloud.todo.form.TodoItemFormData;
import net.passioncloud.todo.models.TodoItem;
import net.passioncloud.todo.repository.TodoItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class TodoItemController {

    private final TodoItemRepository todoItemRepository;

    // inject TodoItemRepository via constructor injection
    public TodoItemController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("someItemsPresent", todoItemRepository.count() != 0);
        model.addAttribute("item", new TodoItemFormData());
        model.addAttribute("todos", getTodoItems());
        return "index";
    }

    private List<TodoItemDto> getTodoItems() {
        return todoItemRepository.findAll()
                .stream()
                .map(todoItem -> new TodoItemDto(
                        todoItem.getId(),
                        todoItem.getTitle(),
                        todoItem.isCompleted()
                ))
                .collect(Collectors.toList());
    }

    public record TodoItemDto(long id, String title, boolean isCompleted) {}



    @PostMapping
    public String addNewTodoItem(@Valid @ModelAttribute("item") TodoItemFormData todoItemFormData) {
        var todoItem = new TodoItem();
        todoItem.setTitle(todoItemFormData.getTitle());
        todoItemRepository.save(todoItem);
        return "redirect:/";
    }
}

