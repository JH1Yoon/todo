package com.sparta.todo.controller;

import com.sparta.todo.dto.CommentRequestDto;
import com.sparta.todo.dto.CommentResponseDto;
import com.sparta.todo.dto.TodoRequestDto;
import com.sparta.todo.dto.TodoResponseDto;
import com.sparta.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/todo")
    public ResponseEntity<TodoResponseDto> createTodo(@RequestBody TodoRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(requestDto));
    }

    @GetMapping("/todo/{id}")
    public ResponseEntity<TodoResponseDto> getTodo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(todoService.getTodo(id));
    }

    @GetMapping("/todo")
    public ResponseEntity<List<TodoResponseDto>> getAllTodoPaging(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageCount) {
        return ResponseEntity.status(HttpStatus.OK).body(todoService.getAllTodoPaging(pageNumber, pageCount));
    }

    @PutMapping("/todo/{id}")
    public ResponseEntity<TodoResponseDto> updateTodo(@PathVariable Long id, @RequestBody TodoRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(todoService.updateTodo(id, requestDto));
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/todo/comment/{id}")
    public ResponseEntity<CommentResponseDto> createCommentToTodo(@PathVariable Long id, @RequestBody CommentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createCommentToTodo(id, requestDto));
    }
}