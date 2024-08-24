package com.sparta.todo.dto;

import com.sparta.todo.entity.UserTodo;
import lombok.Getter;

@Getter
public class TodoUserResponseDto {
    private Long userId;
    private String username;
    private String email;

    public TodoUserResponseDto(UserTodo userTodo) {
        this.userId = userTodo.getUser().getId();
        this.username = userTodo.getUser().getUsername();
        this.email = userTodo.getUser().getEmail();
    }
}
