package com.example.hangangactivity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

  @GetMapping("/")
  public String main() {
    return "main"; // main page
  }

  @GetMapping("/fragments/home")
  public String homeFragment() {
    return "fragments/home :: home";
  }

  @GetMapping("/fragments/company-login")
  public String companyLoginFragment() {
    return "fragments/company-login :: companyLogin";
  }

  @GetMapping("/fragments/company-dashboard")
  public String companyDashboardFragment() {
    return "fragments/company-dashboard :: companyDashboard";
  }

  @GetMapping("/fragments/auth-register")
  public String authRegisterFragment() {
    return "fragments/auth-register :: authRegister";
  }

  @GetMapping("/fragments/company-register")
  public String companyRegisterFragment() {
    return "fragments/company-register :: companyRegister";
  }
}
