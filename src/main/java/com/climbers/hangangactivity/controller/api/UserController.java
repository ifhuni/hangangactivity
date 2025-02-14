package com.climbers.hangangactivity.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.climbers.hangangactivity.model.User;
import com.climbers.hangangactivity.model.UserResponse;
import com.climbers.hangangactivity.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody User request) {
        String email = request.getEmail();
        String password = request.getPassword();
        logger.info("회원가입 요청 - email: {}", email);

        try {
            // 비즈니스 로직 호출
            userService.registerUser(email, password);
            
            // 성공 응답 반환
            UserResponse response = new UserResponse(true, "회원가입이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("회원가입 실패 - email: {}, error: {}", email, e.getMessage());
            
            // 에러 응답 반환
            UserResponse response = new UserResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
