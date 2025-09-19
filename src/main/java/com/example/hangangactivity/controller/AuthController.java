package com.example.hangangactivity.controller;

import java.util.Locale;

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
    public static final String SESSION_COMPANY_USER_NAME = "COMPANY_USER_NAME";
    public static final String SESSION_COMPANY_ROLE = "COMPANY_ROLE";
    public static final String SESSION_COMPANY_ID = "COMPANY_ID";
    public static final String SESSION_COMPANY_MEMBERSHIP_STATUS = "COMPANY_MEMBERSHIP_STATUS";
    public static final String SESSION_COMPANY_NAME = "COMPANY_NAME";

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_UNASSIGNED = "UNASSIGNED";

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
        session.setAttribute(SESSION_COMPANY_USER_NAME, user.getName());
        session.setAttribute(SESSION_COMPANY_ROLE, user.getRole());
        session.setAttribute(SESSION_COMPANY_ID, user.getCompanyId());
        session.setAttribute(SESSION_COMPANY_MEMBERSHIP_STATUS, user.getMembershipStatus());
        session.setAttribute(SESSION_COMPANY_NAME, user.getCompanyName());

        String status = normalizeStatus(user.getMembershipStatus());
        boolean isAdmin = ROLE_ADMIN.equalsIgnoreCase(user.getRole());
        boolean hasCompany = user.getCompanyId() != null;
        boolean approved = STATUS_APPROVED.equals(status);
        boolean pending = STATUS_PENDING.equals(status);
        boolean unassigned = STATUS_UNASSIGNED.equals(status) || !hasCompany;

        boolean requiresCompanySetup = false;
        String nextAction = "DASHBOARD";
        String message = "로그인이 완료되었습니다.";

        if (!isAdmin) {
            if (unassigned) {
                requiresCompanySetup = true;
                nextAction = "REGISTER";
                message = "소속된 업체가 없습니다. 신규 업체를 등록하거나 기존 업체 담당자에게 승인을 요청해주세요.";
            } else if (!approved) {
                requiresCompanySetup = true;
                nextAction = "AWAIT_APPROVAL";
                message = pending
                        ? "업체 승인 대기중입니다. 승인 완료 후 다시 이용해주세요."
                        : "업체 승인 상태를 확인해주세요.";
            }
        }

        AuthResponse response = new AuthResponse(true, message, user.getUsername());
        response.setName(user.getName());
        response.setCompanyId(user.getCompanyId());
        response.setCompanyName(user.getCompanyName());
        response.setMembershipStatus(status);
        response.setRequiresCompanySetup(requiresCompanySetup);
        response.setRole(user.getRole());
        response.setNextAction(nextAction);

        return ResponseEntity.ok(response);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return STATUS_UNASSIGNED;
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}



