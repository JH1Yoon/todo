package com.sparta.todo.service;

import com.sparta.todo.dto.CommentRequestDto;
import com.sparta.todo.dto.CommentResponseDto;
import com.sparta.todo.dto.TodoRequestDto;
import com.sparta.todo.dto.TodoResponseDto;
import com.sparta.todo.entity.Comment;
import com.sparta.todo.entity.Todo;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto) {
        // RequestDto -> Entity
        Todo todo = new Todo(requestDto);

        // DB 저장
        Todo newtodo =  todoRepository.save(todo);

        // Entity -> ResponseDto
        TodoResponseDto todoResponseDto = new TodoResponseDto(newtodo);

        return todoResponseDto;
    }

    public TodoResponseDto getTodo(Long id) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(id);

        TodoResponseDto todoResponseDto = new TodoResponseDto(todo);

        return todoResponseDto;
    }

    public List<TodoResponseDto> getAllTodoPaging(Integer pageNumber, Integer pageCount) {
        Pageable pageable = PageRequest.of(pageNumber, pageCount, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<Todo> todo = todoRepository.findAll(pageable);

        return todo.stream().map(TodoResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto requestDto) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(id);

        // Entity 업데이트
        todo.update(requestDto);

        // 변경된 일정 DB 저장
        todoRepository.save(todo);

        TodoResponseDto todoResponseDto = new TodoResponseDto(todo);

        return todoResponseDto;
    }

    public void deleteTodo(Long id) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(id);

        todoRepository.delete(todo);
    }

    @Transactional
    public CommentResponseDto createCommentToTodo(Long id, CommentRequestDto requestDto) {
        // Todo Entity가 있는지 확인
        Todo todo = findTodo(id);

        // Comment Entity 생성
        Comment comment = new Comment();
        comment.setUsername(requestDto.getUsername());
        comment.setContent(requestDto.getContent());
        comment.setTodo(todo);

        // DB에 저장
        Comment newcomment = commentRepository.save(comment);

        CommentResponseDto commentResponseDto = new CommentResponseDto(newcomment);

        return commentResponseDto;
    }

    private Todo findTodo(Long id) {
        return todoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 일정은 존재하지 않습니다."));
    }
}
