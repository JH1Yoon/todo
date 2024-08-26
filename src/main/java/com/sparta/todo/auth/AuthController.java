package com.sparta.todo.auth;

import com.sparta.todo.dto.LoginRequestDto;
import com.sparta.todo.dto.LoginResponseDto;
import com.sparta.todo.dto.UserRequestDto;
import com.sparta.todo.dto.UserResponseDto;
import com.sparta.todo.entity.User;
import com.sparta.todo.jwt.JwtUtil;
import com.sparta.todo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDto userRequestDto) {
        try {
            UserResponseDto userResponseDto = userService.createUser(userRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
        } catch (Exception e) {
            // 예외 로그 기록
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        try {
            User user = userService.authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            String token = jwtUtil.createToken(user.getUsername());

            // JWT를 Header에 추가
            response.addHeader(HttpHeaders.AUTHORIZATION, token);

            // 응답 본문에 메시지와 상태 코드 반환
            return ResponseEntity.ok(new LoginResponseDto("로그인 성공", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}