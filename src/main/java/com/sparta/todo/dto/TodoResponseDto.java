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
    private String weather;
    private Set<TodoUserResponseDto> users;
    private int commentCount;

    public TodoResponseDto(Todo todo, boolean includeUsers) {
        this.id = todo.getId();
        this.title = todo.getTitle();
        this.content = todo.getContent();
        this.createdAt = todo.getCreatedAt();
        this.modifiedAt = todo.getModifiedAt();
        this.weather = todo.getWeather();
        if (includeUsers) {
            // 지연 로딩을 강제로 트리거하여 userTodos를 로딩
            todo.getUserTodos().size();
            this.users = todo.getUserTodos().stream()
                    .map(TodoUserResponseDto::new)
                    .collect(Collectors.toSet());
        } else {
            this.users = Set.of(); // 전체 조회 시 유저 정보 미포함
        }
        this.commentCount = todo.getComments() != null ? todo.getComments().size() : 0;
    }
}
