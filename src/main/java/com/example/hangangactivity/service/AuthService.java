package com.example.hangangactivity.service;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.LoginRequest;
import com.example.hangangactivity.dto.RegisterRequest;
import com.example.hangangactivity.mapper.CompanyUserMapper;
import com.example.hangangactivity.model.CompanyUser;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private static final String ROLE_COMPANY = "COMPANY";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_UNASSIGNED = "UNASSIGNED";
    private static final String STATUS_PENDING = "PENDING";

    private final CompanyUserMapper companyUserMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CompanyUserMapper companyUserMapper, PasswordEncoder passwordEncoder) {
        this.companyUserMapper = companyUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) {
        final String username = normalize(request.getUsername());
        final String name = normalize(request.getName());
        final String password = request.getPassword();
        final String confirmPassword = request.getConfirmPassword();

        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디를 입력해주세요.");
        }

        if (!StringUtils.hasText(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이름을 입력해주세요.");
        }

        if (!StringUtils.hasText(password) || password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 6자 이상 입력해주세요.");
        }

        if (confirmPassword == null || !password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다.");
        }

        CompanyUser existing = companyUserMapper.findByUsername(username);
        if (existing != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        CompanyUser user = new CompanyUser();
        user.setUsername(username);
        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        user.setCompanyId(null);
        user.setRole(ROLE_COMPANY);
        user.setMembershipStatus(STATUS_UNASSIGNED);

        companyUserMapper.insert(user);
    }

    public CompanyUser login(LoginRequest request) {
        final String username = normalize(request.getUsername());
        final String password = request.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디와 비밀번호를 모두 입력해주세요.");
        }

        CompanyUser user = companyUserMapper.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        user.setRole(normalizeRole(user.getRole()));
        user.setMembershipStatus(normalizeStatus(user.getMembershipStatus()));

        return user;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return ROLE_COMPANY;
        }
        String upper = role.trim().toUpperCase(Locale.ROOT);
        if (ROLE_ADMIN.equals(upper)) {
            return ROLE_ADMIN;
        }
        return ROLE_COMPANY;
    }

    private String normalizeStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return STATUS_UNASSIGNED;
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}





