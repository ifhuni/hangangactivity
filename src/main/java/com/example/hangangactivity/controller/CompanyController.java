package com.example.hangangactivity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hangangactivity.dto.CompanyApproveRequest;
import com.example.hangangactivity.dto.CompanyRegisterRequest;
import com.example.hangangactivity.dto.CompanyRegistrationResponse;
import com.example.hangangactivity.dto.CompanyVerificationRequest;
import com.example.hangangactivity.dto.PendingCompanyRequest;
import com.example.hangangactivity.model.Company;
import com.example.hangangactivity.model.CompanyUser;
import com.example.hangangactivity.service.CompanyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_COMPANY = "COMPANY";

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/register")
    public ResponseEntity<CompanyRegistrationResponse> registerCompany(
            @RequestBody CompanyRegisterRequest request,
            HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute(AuthController.SESSION_COMPANY_USER_ID) == null) {
            return ResponseEntity.status(401).body(
                    new CompanyRegistrationResponse(false, "로그인이 필요합니다."));
        }

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        if (role == null || (!ROLE_COMPANY.equalsIgnoreCase(role) && !ROLE_ADMIN.equalsIgnoreCase(role))) {
            return ResponseEntity.status(403).body(
                    new CompanyRegistrationResponse(false, "업체 등록 권한이 없습니다."));
        }

        CompanyUser updatedUser = companyService.registerCompany(userId, request);

        session.setAttribute(AuthController.SESSION_COMPANY_ID, updatedUser.getCompanyId());
        session.setAttribute(AuthController.SESSION_COMPANY_MEMBERSHIP_STATUS, updatedUser.getMembershipStatus());
        session.setAttribute(AuthController.SESSION_COMPANY_NAME, updatedUser.getCompanyName());

        String message = "업체 등록 요청이 접수되었습니다. 관리자 승인을 기다려 주세요.";
        CompanyRegistrationResponse response = new CompanyRegistrationResponse(true, message,
                updatedUser.getCompanyId(), updatedUser.getMembershipStatus());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<List<PendingCompanyRequest>> listPendingRequests(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(companyService.getPendingRequests());
    }

    @PostMapping("/pending/{userId}/approve")
    public ResponseEntity<Void> approve(@PathVariable("userId") Long userId,
            @RequestBody CompanyApproveRequest approveRequest,
            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }

        if (approveRequest == null || approveRequest.getCompanyId() == null) {
            return ResponseEntity.badRequest().build();
        }

        companyService.approveCompany(userId, approveRequest.getCompanyId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Company>> getAllCompanies(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @PostMapping("/{companyId}/verify")
    public ResponseEntity<Void> verifyCompany(@PathVariable Long companyId,
                                              @RequestBody CompanyVerificationRequest requestBody,
                                              HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            return ResponseEntity.status(403).build();
        }
        companyService.updateCompanyVerificationStatus(companyId, requestBody.isVerified());
        return ResponseEntity.ok().build();
    }

    private boolean isAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }
        if (session.getAttribute(AuthController.SESSION_COMPANY_USER_ID) == null) {
            return false;
        }
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }
}