package com.sparta.todo.service;

import com.sparta.todo.config.PasswordEncoder;
import com.sparta.todo.dto.UserRequestDto;
import com.sparta.todo.dto.UserResponseDto;
import com.sparta.todo.entity.User;
import com.sparta.todo.entity.UserRoleEnum;
import com.sparta.todo.jwt.JwtUtil;
import com.sparta.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 유저 생성
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        String username = userRequestDto.getUsername();
        String password = passwordEncoder.encode(userRequestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = userRequestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;  // 기본값: USER
        if (userRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(userRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }


        User user = new User(username, email, password, role);
        User newUser = userRepository.save(user);

        // JWT 생성
        String token = jwtUtil.createToken(user.getUsername(), role);

        return new UserResponseDto(newUser, token);
    }

    // 로그인 처리
    public UserResponseDto login(String email, String rawPassword) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new Exception("Invalid email or password");
        }

        String token = jwtUtil.createToken(user.getUsername(), user.getRole());

        return new UserResponseDto(user, token);
    }

    // 이메일과 비밀번호로 유저 인증
    public User authenticate(String email, String rawPassword) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new Exception("Invalid email or password");
        }

        return user;
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

        if (userRequestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

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
