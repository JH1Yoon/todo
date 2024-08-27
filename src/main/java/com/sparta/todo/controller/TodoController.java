package com.sparta.todo.controller;

import com.sparta.todo.dto.CommentRequestDto;
import com.sparta.todo.dto.CommentResponseDto;
import com.sparta.todo.dto.TodoRequestDto;
import com.sparta.todo.dto.TodoResponseDto;
import com.sparta.todo.entity.UserRoleEnum;
import com.sparta.todo.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // 일정 생성
    @PostMapping("/todo")
    public ResponseEntity<TodoResponseDto> createTodo(@RequestBody TodoRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(requestDto));
    }

    // 생성된 일정에 담당 유저 추가
    @PostMapping("/todo/{id}/users")
    public ResponseEntity<TodoResponseDto> addUsersToTodo(@PathVariable Long id, @RequestBody Map<String, List<Long>> request, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Long> userIds = request.get("userIds");
        TodoResponseDto updatedTodo = todoService.addUsersToTodo(id, userIds);
        return ResponseEntity.ok(updatedTodo);
    }

    // 특정 id에 해당하는 일정 조회
    @GetMapping("/todo/{id}")
    public ResponseEntity<TodoResponseDto> getTodo(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(todoService.getTodo(id));
    }

    // 모든 일정 조회
    @GetMapping("/todo")
    public ResponseEntity<List<TodoResponseDto>> getAllTodoPaging(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageCount,
            HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(todoService.getAllTodoPaging(pageNumber, pageCount));
    }

    // 특정 id에 해당하는 일정 수정
    @PutMapping("/todo/{id}")
    public ResponseEntity<TodoResponseDto> updateTodo(@PathVariable Long id, @RequestBody TodoRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || !UserRoleEnum.ADMIN.getAuthority().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(todoService.updateTodo(id, requestDto));
    }

    // 특정 id에 해당하는 일정 삭제
    @DeleteMapping("/todo/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || !UserRoleEnum.ADMIN.getAuthority().equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        todoService.deleteTodo(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 특정 id에 해당하는 일정에 댓글 생성
    @PostMapping("/todo/comment/{id}")
    public ResponseEntity<CommentResponseDto> createCommentToTodo(@PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createCommentToTodo(id, requestDto));
    }
}