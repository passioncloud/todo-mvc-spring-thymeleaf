package net.passioncloud.todo.controller;

import net.passioncloud.todo.form.TodoItemFormData;
import net.passioncloud.todo.models.TodoItem;
import net.passioncloud.todo.repository.TodoItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class TodoItemController {

    private final TodoItemRepository todoItemRepository;

    public enum ListFilter { ALL, INCOMPLETE, COMPLETED };

    // inject TodoItemRepository via constructor injection
    public TodoItemController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    private void addAttributesForIndex(Model model, ListFilter listFilter) {
        model.addAttribute("someItemsPresent", todoItemRepository.count() != 0);
        model.addAttribute("item", new TodoItemFormData());
        model.addAttribute("todos", getTodoItems(listFilter));
        model.addAttribute("incompleteTodoItemCount", todoItemRepository.countAllByCompleted(false));
        model.addAttribute("todoItemCount", todoItemRepository.count());
        model.addAttribute("listFilter", listFilter);
        model.addAttribute("someCompletedTodosPresent", todoItemRepository.existsTodoItemByCompleted(true));
    }
    @GetMapping("/")
    public String index(Model model) {
        addAttributesForIndex(model, ListFilter.ALL);
        return "index";
    }

    @GetMapping("/completed")
    public String completed(Model model) {
        addAttributesForIndex(model, ListFilter.COMPLETED);
        return "index";
    }
    @GetMapping("/incomplete")
    public String incomplete(Model model){
        addAttributesForIndex(model, ListFilter.INCOMPLETE);
        return "index";
    }

    private List<TodoItemDto> getTodoItems(ListFilter listFilter) {
        List<TodoItem> todoItems = switch (listFilter) {
            case ALL -> todoItemRepository.findAll();
            case COMPLETED -> todoItemRepository.findAllByCompleted(true);
            case INCOMPLETE -> todoItemRepository.findAllByCompleted(false);
        };
        return todoItems
                .stream()
                .map(TodoItemDto::fromTodoItem)
                .collect(Collectors.toList());
    }

    public record TodoItemDto(long id, String title, boolean isCompleted) {
        static TodoItemDto fromTodoItem(TodoItem todoItem) {
            return new TodoItemDto(
                    todoItem.getId(),
                    todoItem.getTitle(),
                    todoItem.isCompleted()
            );
        }
    }


//    @PostMapping
//    public String addNewTodoItem(@Valid @ModelAttribute("item") TodoItemFormData todoItemFormData) {
//        var todoItem = new TodoItem();
//        todoItem.setTitle(todoItemFormData.getTitle());
//        todoItemRepository.save(todoItem);
//        return "redirect:/";
//    }

    @PutMapping("/{id}/toggle")
    public String toggleSelection(@PathVariable("id") Long id) {
        TodoItem todoItem = todoItemRepository.getById(id);
        todoItem.setCompleted(!todoItem.isCompleted());
        todoItemRepository.save(todoItem);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteTodoItem(@PathVariable("id") Long id) {
        todoItemRepository.deleteById(id);
        return "redirect:/";
    }

    @DeleteMapping("/completed")
    public String deleteCompletedTodoItems() {
        todoItemRepository.deleteAll(todoItemRepository.findAllByCompleted(true));
        return "redirect:/";
    }

    @PostMapping(headers = "HX-Request")
    public String htmxAddTodoItem(
            TodoItemFormData formData,
            Model model,
            HttpServletResponse response) {
        System.out.println("HX-Request made to add an item with title: " + formData.getTitle());
        TodoItem todoItem = new TodoItem();
        todoItem.setTitle(formData.getTitle());
        todoItem = todoItemRepository.save(todoItem);
        model.addAttribute("item", TodoItemDto.fromTodoItem(todoItem));
        response.setHeader("HX-Trigger", "itemAdded");
        return "frag_todo :: frag_todo";
    }

    @GetMapping(path = "/items-count", headers="HX-Request")
    public String htmxTodoItemCount(Model model) {
        model.addAttribute("incompleteTodoItemCount", todoItemRepository.countAllByCompleted(false));
        model.addAttribute("todoItemCount", todoItemRepository.count());
        return "frag_count :: active_item_count";
    }

    @PutMapping(value="/{id}/toggle", headers="HX-Request")
    public String htmxToggleTodoItem(@PathVariable("id") Long id,
                                     Model model,
                                     HttpServletResponse response) {
        TodoItem todoItem = todoItemRepository.getById(id);
        todoItem.setCompleted(!todoItem.isCompleted());
        todoItem = todoItemRepository.save(todoItem);
        model.addAttribute("item", TodoItemDto.fromTodoItem(todoItem));
        response.setHeader("HX-Trigger", "itemCompletionToggled");
        return "frag_todo :: frag_todo";
    }

    @DeleteMapping(value="/{id}", headers = "HX-Request")
    public String htmxDeleteTodoItem(
            @PathVariable("id") Long id,
            HttpServletResponse response) {
        todoItemRepository.deleteById(id);
        response.setHeader("HX-Trigger", "itemDeleted");
        return "_blank :: _blank";
    }
}

