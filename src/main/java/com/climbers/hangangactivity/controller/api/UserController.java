package com.climbers.hangangactivity.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.climbers.hangangactivity.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,  @RequestParam String password) {
        try {
            userService.registerUser(email, password);
            return "redirect:/company/login";
        } catch (IllegalArgumentException e) {
            return "redirect:/company/register?error=" + e.getMessage();
        }
    }
}

