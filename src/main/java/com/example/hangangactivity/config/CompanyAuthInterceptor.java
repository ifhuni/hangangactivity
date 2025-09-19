package com.example.hangangactivity.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.hangangactivity.controller.AuthController;
import com.example.hangangactivity.mapper.CompanyUserMapper;
import com.example.hangangactivity.model.CompanyUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CompanyAuthInterceptor implements HandlerInterceptor {

    private static final String AUTH_REQUIRED_MESSAGE = "로그?�이 ?�요?�니??";
    private static final String COMPANY_APPROVAL_REQUIRED_MESSAGE = "?�속???�체 ?�인 ???�용?????�습?�다.";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_APPROVED = "APPROVED";

    private final CompanyUserMapper companyUserMapper;

    public CompanyAuthInterceptor(CompanyUserMapper companyUserMapper) {
        this.companyUserMapper = companyUserMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthController.SESSION_COMPANY_USER_ID) == null) {
            respond(response, HttpServletResponse.SC_UNAUTHORIZED, AUTH_REQUIRED_MESSAGE);
            return false;
        }

        String role = asString(session.getAttribute(AuthController.SESSION_COMPANY_ROLE));
        String status = asString(session.getAttribute(AuthController.SESSION_COMPANY_MEMBERSHIP_STATUS));
        Object companyId = session.getAttribute(AuthController.SESSION_COMPANY_ID);
        Long userId = asLong(session.getAttribute(AuthController.SESSION_COMPANY_USER_ID));

        boolean isAdmin = ROLE_ADMIN.equalsIgnoreCase(role);
        boolean hasCompany = companyId != null;
        boolean approved = STATUS_APPROVED.equalsIgnoreCase(status);

        if (!isAdmin && !approved && userId != null) {
            CompanyUser refreshed = companyUserMapper.findById(userId);
            if (refreshed != null) {
                session.setAttribute(AuthController.SESSION_COMPANY_USER_NAME, refreshed.getName());
                if (refreshed.getCompanyId() != null) {
                    session.setAttribute(AuthController.SESSION_COMPANY_ID, refreshed.getCompanyId());
                    session.setAttribute(AuthController.SESSION_COMPANY_NAME, refreshed.getCompanyName());
                }
                String refreshedStatus = asString(refreshed.getMembershipStatus());
                session.setAttribute(AuthController.SESSION_COMPANY_MEMBERSHIP_STATUS, refreshedStatus);

                companyId = refreshed.getCompanyId();
                hasCompany = companyId != null;
                approved = STATUS_APPROVED.equalsIgnoreCase(refreshedStatus);
            }
        }

        if (!isAdmin && (!hasCompany || !approved)) {
            respond(response, HttpServletResponse.SC_FORBIDDEN, COMPANY_APPROVAL_REQUIRED_MESSAGE);
            return false;
        }

        return true;
    }

    private void respond(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
        response.getWriter().flush();
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}
