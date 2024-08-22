package com.sparta.todo;

import com.sparta.todo.dto.TodoResponseDto;
import com.sparta.todo.entity.Comment;
import com.sparta.todo.entity.Todo;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.TodoRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
class TodoApplicationTests {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    CommentRepository commentRepository;


    @Test
    @Disabled
    @DisplayName("todo에 있는 List<Comment에 데이터가 있는지 확인")
    void contextLoads() {
        Todo todo = todoRepository.findById(1L).orElse(null);

        // 고객 정보 조회
        System.out.println("user.getName() = " + todo.getUsername());

        List<Comment> comments = todo.getComments();

        for (Comment comment : comments) {
            System.out.println(comment.getId());
            System.out.println(comment.getUsername());
            System.out.println(comment.getContent());
            System.out.println(comment.getTodo().getId());
        }
    }

}
