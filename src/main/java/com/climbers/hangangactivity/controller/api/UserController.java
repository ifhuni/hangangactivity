package com.climbers.hangangactivity.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.climbers.hangangactivity.model.User;
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
    public String registerUser(@RequestBody User request) {
        String email = request.getEmail();
        String password = request.getPassword();
        logger.debug("[controller] email : " + email);
        logger.debug("[controller] password : " + password);
        try {
            // 비즈니스 로직 호출
            userService.registerUser(email, password);
            return "redirect:/company/login";  // 성공적으로 등록된 경우 로그인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            // 에러 메시지 전달
            return "redirect:/company/register?error=" + e.getMessage();
        }
    }
}
