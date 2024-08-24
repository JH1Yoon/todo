package com.sparta.todo.service;

import com.sparta.todo.dto.UserRequestDto;
import com.sparta.todo.dto.UserResponseDto;
import com.sparta.todo.entity.User;
import com.sparta.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 유저 생성
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = new User(userRequestDto);

        User newUser = userRepository.save(user);

        return new UserResponseDto(newUser);
    }

    // 특정 id에 해당하는 유저 조회
    public UserResponseDto getUser(Long id) {
        User user = findUser(id);

        return new UserResponseDto(user);
    }

    // 모든 유저 조회
    public List<UserResponseDto> getAllUser() {
        return userRepository.findAllByOrderByModifiedAtDesc().stream()
                .map(UserResponseDto::new)
                .toList();
    }

    // 특정 id에 해당하는 유저 수정
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = findUser(id);

        user.update(userRequestDto);

        userRepository.save(user);

        return new UserResponseDto(user);
    }

    // 특정 id에 해당하는 유저 삭제
    @Transactional
    public void deleteUser(Long id) {
        User user = findUser(id);

        userRepository.delete(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 유저가 존재하지 않습니다."));
    }
}
