package net.passioncloud.todo.controller;

import net.passioncloud.todo.repository.TodoItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("totalNumberOfItems", todoItemRepository.count());
        model.addAttribute("someItemsPresent", todoItemRepository.count() != 0);
        return "index";
    }
}
