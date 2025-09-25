package com.example.hangangactivity.controller;

import java.util.Locale;

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
  public String companyDashboardFragment(HttpSession session, Model model,
      @RequestParam(value = "companyName", required = false) String companyNameParam) {
    String role = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE) : null;
    String status = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_MEMBERSHIP_STATUS) : null;
    String sessionCompanyName = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_NAME) : null;
    String sessionCompanyUserName = session != null ? (String) session.getAttribute(AuthController.SESSION_COMPANY_USER_NAME) : null;
    Long sessionCompanyId = session != null ? (Long) session.getAttribute(AuthController.SESSION_COMPANY_ID) : null;

    String normalizedRole = role != null ? role.trim().toUpperCase(Locale.ROOT) : "COMPANY";
    String normalizedStatus = status != null ? status.trim().toUpperCase(Locale.ROOT) : "UNASSIGNED";

    String effectiveName = null;
    if (sessionCompanyName != null && !sessionCompanyName.isBlank()) {
      effectiveName = sessionCompanyName;
    } else if (companyNameParam != null && !companyNameParam.isBlank()) {
      effectiveName = companyNameParam.trim();
    }
    if (effectiveName == null || effectiveName.isBlank()) {
      effectiveName = "?낆껜";
    }

    model.addAttribute("companyRole", normalizedRole);
    model.addAttribute("companyStatus", normalizedStatus);
    model.addAttribute("companyName", effectiveName);
    model.addAttribute("companyUserName", sessionCompanyUserName);
    model.addAttribute("companyId", sessionCompanyId);
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

