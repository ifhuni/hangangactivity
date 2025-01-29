package com.climbers.hangangactivity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @GetMapping({"", "/"}) // 빈 경로와 "/" 둘 다 매핑
    public String index(Model model) {
        model.addAttribute("message", "Welcome to Thymeleaf!");
        return "company"; // templates 폴더의 company.html을 렌더링
    }
}
