package com.example.hangangactivity.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.hangangactivity.controller.AuthController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CompanyAuthInterceptor implements HandlerInterceptor {

    private static final String AUTH_REQUIRED_MESSAGE = "로그인이 필요합니다.";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        boolean authenticated = session != null
                && session.getAttribute(AuthController.SESSION_COMPANY_USER_ID) != null;

        if (!authenticated) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(AUTH_REQUIRED_MESSAGE);
            response.getWriter().flush();
            return false;
        }

        return true;
    }
}
