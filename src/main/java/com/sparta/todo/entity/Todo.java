package com.sparta.todo.entity;

import com.sparta.todo.dto.TodoRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "todo")
public class Todo extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "title", nullable = false)
    private String title;
    private String content;

    @OneToMany(mappedBy = "todo", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> comments;

    public Todo(TodoRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }

    public void update(TodoRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }
}
