package com.example.hangangactivity.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.ReservationParticipantResponse;
import com.example.hangangactivity.dto.ReservationPendingResponse;
import com.example.hangangactivity.service.ReservationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/reservations")
public class ReservationApiController {

    private final ReservationService reservationService;

    public ReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReservationPendingResponse>> listPending(@RequestParam(value = "companyId", required = false) Long companyId,
                                                                        HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        List<ReservationPendingResponse> result = reservationService.listPendingForCompany(userId, role, companyId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ReservationParticipantResponse>> listParticipantsByActivity(@PathVariable("activityId") Long activityId,
                                                                                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        List<ReservationParticipantResponse> result = reservationService.listParticipantsForActivity(userId, role, activityId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{reservationId}/approve")
    public ResponseEntity<ReservationPendingResponse> approve(@PathVariable("reservationId") Long reservationId,
                                                              HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        ReservationPendingResponse response = reservationService.approve(userId, role, reservationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reservationId}/reject")
    public ResponseEntity<ReservationPendingResponse> reject(@PathVariable("reservationId") Long reservationId,
                                                             HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        ReservationPendingResponse response = reservationService.reject(userId, role, reservationId);
        return ResponseEntity.ok(response);
    }

    private void ensureAuthenticated(HttpSession session) {
        if (session == null || session.getAttribute(AuthController.SESSION_COMPANY_USER_ID) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required.");
        }
    }
}
