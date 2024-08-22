package com.sparta.todo.service;

import com.sparta.todo.dto.CommentRequestDto;
import com.sparta.todo.dto.CommentResponseDto;
import com.sparta.todo.entity.Comment;
import com.sparta.todo.entity.Todo;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public CommentResponseDto createComment(Long todoId, CommentRequestDto requestDto) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(todoId);

        // Comment Entity 생성
        Comment comment = new Comment();
        comment.setUsername(requestDto.getUsername());
        comment.setContent(requestDto.getContent());
        comment.setTodo(todo);


        // DB에 저장
        Comment newcomment = commentRepository.save(comment);

        // Entity -> ResponseDto
        CommentResponseDto commentResponseDto = new CommentResponseDto(newcomment);

        return commentResponseDto;

    }

    public CommentResponseDto getComment(Long id) {
        // 해당하는 댓글이 있는지 확인
        Comment comment = findComment(id);

        // Entity -> ResponseDto
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        return commentResponseDto;
    }

    public List<CommentResponseDto> getAllComments() {
        return commentRepository.findAllByOrderByModifiedAtDesc().stream().map(CommentResponseDto::new).toList();
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto) {
        // 해당하는 댓글이 있는지 확인
        Comment comment = findComment(id);

        // Entity 업데이트
        comment.update(requestDto);

        // 변경될 댓글 DB에 저장
        commentRepository.save(comment);

        // Entity -> ResponseDto
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        return commentResponseDto;
    }

    @Transactional
    public void deleteComment(Long id) {
        // 해당하는 댓글이 있는지 확인
        Comment comment = findComment(id);

        commentRepository.delete(comment);
    }

    private Todo findTodo(Long id) {
        return todoRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 일정은 존재하지 않습니다."));
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 댓글은 존재하지 않습니다."));
    }
}
