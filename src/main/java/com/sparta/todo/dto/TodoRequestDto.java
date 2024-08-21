package com.sparta.todo.dto;

import lombok.Getter;

@Getter
public class TodoRequestDto {
    private String username;
    private String title;
    private String content;
}
