package com.example.hangangactivity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hangangactivity.dto.AuthResponse;
import com.example.hangangactivity.dto.LoginRequest;
import com.example.hangangactivity.dto.RegisterRequest;
import com.example.hangangactivity.model.CompanyUser;
import com.example.hangangactivity.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String SESSION_COMPANY_USER_ID = "COMPANY_USER_ID";
    public static final String SESSION_COMPANY_USERNAME = "COMPANY_USERNAME";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new AuthResponse(true, "회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        CompanyUser user = authService.login(request);
        session.setAttribute(SESSION_COMPANY_USER_ID, user.getId());
        session.setAttribute(SESSION_COMPANY_USERNAME, user.getUsername());
        return ResponseEntity.ok(new AuthResponse(true, "로그인이 완료되었습니다.", user.getUsername()));
    }
}
