package com.climbers.hangangactivity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @GetMapping({"", "/"})
    public String index(Model model) {
        model.addAttribute("message", "Welcome to Thymeleaf!");
        return "company/login";
    }
}
