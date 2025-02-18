package com.climbers.hangangactivity.controller.api;

import com.climbers.hangangactivity.model.LoginResponse;
import com.climbers.hangangactivity.model.User;
import com.climbers.hangangactivity.security.JwtUtil;
import com.climbers.hangangactivity.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        System.out.println("로그인 접근");

        // 이메일과 비밀번호 가져오기
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // 인증을 위한 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        System.out.println("1. authentication : ");
        System.out.println(authentication);
        // 인증 성공 후 JWT 생성
        String token = jwtUtil.generateToken(authentication); // 인증된 객체를 전달하여 JWT 생성
        System.out.println("2. token : ");
        System.out.println(token);
        // JWT 토큰 반환
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        System.out.println("회원가입 접근");
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Map<String, String> response = new HashMap<>();
        response.put("email", userDetails.getUsername()); // email 정보 반환

        return ResponseEntity.ok(response);
    }
}
