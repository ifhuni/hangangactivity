package com.climbers.hangangactivity.controller.api;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.climbers.hangangactivity.controller.company.CompanyPageController;
import com.climbers.hangangactivity.model.User;
import com.climbers.hangangactivity.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyPageController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String email = request.get("email");
        logger.debug(email);
        String password = request.get("password");
        logger.debug(password);
        User user = userService.authenticate(email, password);

        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }
}