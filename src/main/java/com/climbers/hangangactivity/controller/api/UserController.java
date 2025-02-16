package com.climbers.hangangactivity.controller.api;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.climbers.hangangactivity.model.User;
import com.climbers.hangangactivity.model.UserResponse;
import com.climbers.hangangactivity.service.UserService;

import jakarta.servlet.http.HttpSession;

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
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        String password = request.get("password");
    
        User user = userService.authenticate(email, password);
    
        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화

        // 캐시 방지 설정 추가
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(Collections.singletonMap("success", true));
    }


    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user != null) {
            return ResponseEntity.ok(Collections.singletonMap("email", user.getEmail()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "로그인이 필요합니다."));
        }
    }
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.ok(Collections.singletonMap("loggedIn", false));
        } else {
            return ResponseEntity.ok(Collections.singletonMap("loggedIn", true));
        }
    }
}
