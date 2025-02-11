package com.climbers.hangangactivity.controller.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.climbers.hangangactivity.mapper.UserMapper;
import com.climbers.hangangactivity.model.User;

/**
 * 회사 관련 페이지들을 처리하는 컨트롤러
 */
@Controller
@RequestMapping("/company")
public class CompanyPageController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyPageController.class);

    @Autowired
    private UserMapper userMapper;

    /**
     * 회사 로그인 페이지를 보여줍니다.
     */
    @GetMapping("/")
    public String showCompanyLoginPage() {
        logger.info("Accessing company login page");
        return "company/login";  // 회사 로그인 페이지 템플릿 반환
    }

    /**
     * 회사 회원가입 페이지를 보여줍니다.
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        logger.info("Accessing company register page");
        return "company/register";  // 회원가입 페이지 템플릿 반환
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email, 
                               @RequestParam String password) {

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        // 회원가입 처리
        userMapper.insertUser(user);

        return "redirect:/company/login"; // 로그인 페이지로 리디렉션
    }
}
