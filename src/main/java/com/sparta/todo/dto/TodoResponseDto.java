package com.sparta.todo.dto;

import com.sparta.todo.entity.Todo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class TodoResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Set<String> userNames;
    private int commentCount;

    public TodoResponseDto(Todo todo) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.content = todo.getContent();
        this.createdAt = todo.getCreatedAt();
        this.modifiedAt = todo.getModifiedAt();
        this.userNames = todo.getUserTodos().stream()
                .map(userTodo -> userTodo.getUser().getUsername())
                .collect(Collectors.toSet());
        this.commentCount = todo.getComments() != null ? todo.getComments().size() : 0;
    }
}
