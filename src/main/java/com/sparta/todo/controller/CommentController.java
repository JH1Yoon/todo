package com.sparta.todo.controller;

import com.sparta.todo.dto.CommentRequestDto;
import com.sparta.todo.dto.CommentResponseDto;
import com.sparta.todo.entity.UserRoleEnum;
import com.sparta.todo.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 특정 id에 해당하는 일정의 댓글 생성
    @PostMapping("/comment/{todoId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long todoId, @RequestBody CommentRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(todoId, requestDto));
    }

    // 특정 id에 해당하는 댓글 조회
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getComment(id));
    }

    // 모든 댓글 조회
    @GetMapping("/comment")
    public ResponseEntity<List<CommentResponseDto>> getAllComments(HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllComments());
    }

    // 특정 id에 해당하는 댓글 수정
    @PutMapping("/comment/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(id, requestDto));
    }

    // 특정 id에 해당하는 댓글 삭제
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        String role = (String) httpServletRequest.getAttribute("role");
        if (role == null || (!UserRoleEnum.USER.getAuthority().equals(role) && !UserRoleEnum.ADMIN.getAuthority().equals(role))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
