package com.example.hangangactivity.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.CompanyRegisterRequest;
import com.example.hangangactivity.dto.PendingCompanyRequest;
import com.example.hangangactivity.mapper.CompanyMapper;
import com.example.hangangactivity.mapper.CompanyUserMapper;
import com.example.hangangactivity.model.Company;
import com.example.hangangactivity.model.CompanyUser;

@Service
public class CompanyService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_COMPANY = "COMPANY";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_UNASSIGNED = "UNASSIGNED";

    private final CompanyMapper companyMapper;
    private final CompanyUserMapper companyUserMapper;

    public CompanyService(CompanyMapper companyMapper, CompanyUserMapper companyUserMapper) {
        this.companyMapper = companyMapper;
        this.companyUserMapper = companyUserMapper;
    }

    @Transactional
    public CompanyUser registerCompany(Long userId, CompanyRegisterRequest request) {
        CompanyUser user = companyUserMapper.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        if (ROLE_ADMIN.equalsIgnoreCase(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "관리자 계정에서는 업체 등록을 진행할 수 없습니다.");
        }

        String currentStatus = normalize(user.getMembershipStatus());
        if (user.getCompanyId() != null && STATUS_APPROVED.equalsIgnoreCase(currentStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 승인된 업체가 있습니다.");
        }

        if (STATUS_PENDING.equalsIgnoreCase(currentStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 승인 대기 중인 업체가 있습니다.");
        }

        final String companyName = requireText(request.getCompanyName(), "업체명을 입력해 주세요.");
        final String businessNumber = requireText(request.getBusinessNumber(), "사업자등록번호를 입력해 주세요.");
        final String ceoName = requireText(request.getCeoName(), "대표자 이름을 입력해 주세요.");
        final String ceoContact = requireText(request.getCeoContact(), "대표자 연락처를 입력해 주세요.");
        final String officeAddress = requireText(request.getOfficeAddress(), "사무실 주소를 입력해 주세요.");

        Company company = new Company();
        company.setCompanyName(companyName.trim());
        company.setBusinessNumber(businessNumber.trim());
        company.setCeoName(ceoName.trim());
        company.setCeoContact(ceoContact.trim());
        company.setOfficeAddress(officeAddress.trim());
        company.setOfficeContact(trimOrNull(request.getOfficeContact()));
        company.setVerified(Boolean.FALSE);

        companyMapper.insert(company);
        companyUserMapper.updateMembershipOnRegister(userId, company.getId(), STATUS_PENDING);

        return companyUserMapper.findById(userId);
    }

    public List<PendingCompanyRequest> getPendingRequests() {
        return companyUserMapper.findPendingRequests();
    }

    public List<PendingCompanyRequest> getPendingRequestsForUser(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return companyUserMapper.findPendingRequests().stream()
                .filter(item -> userId.equals(item.getUserId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveCompany(Long targetUserId, Long companyId) {
        CompanyUser user = companyUserMapper.findById(targetUserId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다.");
        }

        if (user.getCompanyId() == null || !user.getCompanyId().equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자의 업체 정보가 일치하지 않습니다.");
        }

        String status = normalize(user.getMembershipStatus());
        if (!STATUS_PENDING.equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인 대기 상태인 요청만 처리할 수 있습니다.");
        }

        companyMapper.updateVerification(companyId, true);
        companyUserMapper.updateMembershipStatus(targetUserId, STATUS_APPROVED);
    }

    public List<Company> getAllCompanies() {
        return companyMapper.findAll();
    }

    @Transactional
    public void updateCompanyVerificationStatus(Long companyId, boolean isVerified) {
        companyMapper.updateVerificationStatus(companyId, isVerified);
    }

    private String trimOrNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return value;
    }

    private String normalize(String value) {
        return value == null ? STATUS_UNASSIGNED : value.trim();
    }
}
