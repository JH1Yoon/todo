package com.sparta.todo.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TodoRequestDto {
    private Long userId;
    private List<Long> userIds; // 담당 유저 IDs
    private String title;
    private String content;
}