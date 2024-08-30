package com.sparta.todo.controller;

import com.sparta.todo.dto.LoginRequestDto;
import com.sparta.todo.dto.LoginResponseDto;
import com.sparta.todo.dto.UserRequestDto;
import com.sparta.todo.dto.UserResponseDto;
import com.sparta.todo.entity.UserRoleEnum;
import com.sparta.todo.jwt.AuthUtil;
import com.sparta.todo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 생성
    @PostMapping("/user/signup")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequestDto));
    }

    // 로그인
    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            UserResponseDto userResponseDto = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            String token = userResponseDto.getToken();

            // JWT를 Header에 추가
            response.addHeader(HttpHeaders.AUTHORIZATION, token);

            // 응답 본문에 사용자 정보와 상태 코드 반환
            return ResponseEntity.ok(new LoginResponseDto("로그인 성공", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }


    // 특정 id에 해당하는 유저 조회
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        if (!AuthUtil.hasAnyRole(httpServletRequest, UserRoleEnum.USER, UserRoleEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    // 모든 유저 조회
    @GetMapping("/user")
    public ResponseEntity<List<UserResponseDto>> getAllUser(HttpServletRequest httpServletRequest) {
        if (!AuthUtil.hasAnyRole(httpServletRequest, UserRoleEnum.USER, UserRoleEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());
    }

    // 특정 id에 해당하는 유저 수정
    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto, HttpServletRequest httpServletRequest) {
        if (!AuthUtil.hasAnyRole(httpServletRequest, UserRoleEnum.USER, UserRoleEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, userRequestDto));
    }

    // 특정 id에 해당하는 유저 삭제
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        if (!AuthUtil.hasAnyRole(httpServletRequest, UserRoleEnum.USER, UserRoleEnum.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
