package com.example.hangangactivity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

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
  public String companyDashboardFragment(HttpSession session,Model model,@RequestParam(value = "companyName", required = false) String companyNameParam) {
    String role = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE) : null;
    String status = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_MEMBERSHIP_STATUS) : null;
    String sessionCompanyName = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_NAME) : null;
    String sessionCompanyUserName = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_USER_NAME) : null;

    String effectiveName = sessionCompanyName != null ? sessionCompanyName : companyNameParam;

    model.addAttribute("companyRole", role != null ? role : "COMPANY");
    model.addAttribute("companyStatus", status != null ? status : "UNASSIGNED");
    model.addAttribute("companyName", effectiveName != null ? effectiveName : "?낆껜");
    model.addAttribute("companyUserName", sessionCompanyUserName);
    System.out.println(model);
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
