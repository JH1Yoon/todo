package com.sparta.todo.service;

import com.sparta.todo.dto.CommentRequestDto;
import com.sparta.todo.dto.CommentResponseDto;
import com.sparta.todo.dto.TodoRequestDto;
import com.sparta.todo.dto.TodoResponseDto;
import com.sparta.todo.entity.Comment;
import com.sparta.todo.entity.Todo;
import com.sparta.todo.entity.User;
import com.sparta.todo.entity.UserTodo;
import com.sparta.todo.repository.CommentRepository;
import com.sparta.todo.repository.TodoRepository;
import com.sparta.todo.repository.UserRepository;
import com.sparta.todo.repository.UserTodoRepository;
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
    private final UserRepository userRepository;
    private final UserTodoRepository userTodoRepository;
    private final WeatherService weatherService;

    // 일정 생성
    @Transactional
    public TodoResponseDto createTodo(TodoRequestDto requestDto) {
        // 날씨 정보 조회
        String weather = weatherService.getWeatherToday();

        // User Entity 조회
        User user = findUser(requestDto.getUserId());

        // RequestDto -> Entity
        Todo todo = new Todo(requestDto.getTitle(), requestDto.getContent(), weather);

        // DB 저장
        Todo newTodo = todoRepository.save(todo);

        // UserTodo 관계 설정
        UserTodo userTodo = new UserTodo();
        userTodo.setTodo(newTodo);
        userTodo.setUser(user);
        userTodoRepository.save(userTodo);

        // 추가 담당 유저 설정
        if (requestDto.getUserIds() != null) {
            List<User> additionalUsers = userRepository.findAllById(requestDto.getUserIds());
            for (User additionalUser : additionalUsers) {
                // 중복된 담당 유저 추가 방지
                if (userTodoRepository.findByTodoAndUser(newTodo, additionalUser) == null) {
                    UserTodo additionalUserTodo = new UserTodo();
                    additionalUserTodo.setTodo(newTodo);
                    additionalUserTodo.setUser(additionalUser);
                    userTodoRepository.save(additionalUserTodo);
                }
            }
        }

        return new TodoResponseDto(newTodo, false);
    }

    // 생성된 일정에 담당 유저 추가
    @Transactional
    public TodoResponseDto addUsersToTodo(Long todoId, List<Long> userIds) {
        // 해당 일정 조회
        Todo todo = findTodo(todoId);

        // 추가 담당 유저 설정
        for (Long userId : userIds) {
            User user = findUser(userId);
            UserTodo userTodo = new UserTodo();
            userTodo.setTodo(todo);
            userTodo.setUser(user);

            // 중복된 담당 유저 추가 방지
            if (userTodoRepository.findByTodoAndUser(todo, user) == null) {
                userTodoRepository.save(userTodo);
            }
        }

        return new TodoResponseDto(findTodo(todoId), false);
    }

    // 특정 id에 해당하는 일정 조회
    public TodoResponseDto getTodo(Long id) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(id);

        // 지연 로딩을 강제로 트리거하여 userTodos를 로딩
        todo.getUserTodos().size();

        return new TodoResponseDto(todo, true);
    }

    // 모든 일정 조회
    public List<TodoResponseDto> getAllTodoPaging(Integer pageNumber, Integer pageCount) {
        Pageable pageable = PageRequest.of(pageNumber, pageCount, Sort.by(Sort.Direction.DESC, "modifiedAt"));
        Page<Todo> todos = todoRepository.findAll(pageable);

        return todos.stream()
                .map(todo -> new TodoResponseDto(todo, false))
                .collect(Collectors.toList());
    }

    // 특정 id에 해당하는 일정 수정
    @Transactional
    public TodoResponseDto updateTodo(Long id, TodoRequestDto requestDto) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(id);

        // 일정 업데이트
        todo.update(requestDto);

        // 기존 담당 유저 제거
        userTodoRepository.deleteByTodo(todo);

        // 새로운 담당 유저 추가
        for (Long userId : requestDto.getUserIds()) {
            User user = findUser(userId);
            UserTodo userTodo = new UserTodo();
            userTodo.setTodo(todo);
            userTodo.setUser(user);

            // 중복된 담당 유저 추가 방지
            if (userTodoRepository.findByTodoAndUser(todo, user) == null) {
                userTodoRepository.save(userTodo);
            }
        }

        // 변경된 일정 DB 저장
        todoRepository.save(todo);

        return new TodoResponseDto(todo, false);
    }

    // 특정 id에 해당하는 일정 삭제
    @Transactional
    public void deleteTodo(Long id) {
        // 해당하는 일정이 있는지 확인
        Todo todo = findTodo(id);

        todoRepository.delete(todo);
    }

    // 특정 id에 해당하는 일정에 댓글 생성
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

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 담담자가 존재하지 않습니다."));
    }
}
