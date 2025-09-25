package com.example.hangangactivity.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.ReservationPendingResponse;
import com.example.hangangactivity.mapper.CompanyUserMapper;
import com.example.hangangactivity.mapper.ReservationMapper;
import com.example.hangangactivity.model.CompanyUser;
import com.example.hangangactivity.model.ReservationPending;

@Service
public class ReservationService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELED = "CANCELED";

    private final ReservationMapper reservationMapper;
    private final CompanyUserMapper companyUserMapper;

    public ReservationService(ReservationMapper reservationMapper, CompanyUserMapper companyUserMapper) {
        this.reservationMapper = reservationMapper;
        this.companyUserMapper = companyUserMapper;
    }

    public List<ReservationPendingResponse> listPendingForCompany(Long requesterUserId,
                                                                  String requesterRole,
                                                                  Long companyId) {
        CompanyUser user = requireUser(requesterUserId);
        boolean isAdmin = isAdmin(requesterRole);

        if (companyId == null) {
            if (!isAdmin) {
                companyId = user.getCompanyId();
            } else {
                return reservationMapper.findAllPending().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
            }
        }

        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company id is required.");
        }

        if (!isAdmin) {
            if (user.getCompanyId() == null || !user.getCompanyId().equals(companyId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view reservations for your company.");
            }
        }

        return reservationMapper.findPendingByCompanyId(companyId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReservationPendingResponse> listPendingForCurrentUser(Long requesterUserId, String requesterRole) {
        CompanyUser user = requireUser(requesterUserId);
        if (isAdmin(requesterRole)) {
            return reservationMapper.findAllPending().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }
        if (user.getCompanyId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company registration is required.");
        }
        return reservationMapper.findPendingByCompanyId(user.getCompanyId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationPendingResponse approve(Long requesterUserId, String requesterRole, Long reservationId) {
        return updateStatus(requesterUserId, requesterRole, reservationId, STATUS_CONFIRMED);
    }

    @Transactional
    public ReservationPendingResponse reject(Long requesterUserId, String requesterRole, Long reservationId) {
        return updateStatus(requesterUserId, requesterRole, reservationId, STATUS_CANCELED);
    }

    private ReservationPendingResponse updateStatus(Long requesterUserId,
                                                    String requesterRole,
                                                    Long reservationId,
                                                    String targetStatus) {
        CompanyUser user = requireUser(requesterUserId);
        ReservationPending reservation = reservationMapper.findPendingById(reservationId);
        if (reservation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found or already processed.");
        }

        Long reservationCompanyId = reservation.getCompanyId() != null ? reservation.getCompanyId().longValue() : null;
        if (!isAdmin(requesterRole)) {
            if (user.getCompanyId() == null || !user.getCompanyId().equals(reservationCompanyId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage reservations for your company.");
            }
        }

        String dbStatus = targetStatus.toLowerCase(Locale.ROOT);
        int updated = reservationMapper.updateStatus(reservationId, dbStatus);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reservation status has already changed.");
        }

        reservation.setStatus(dbStatus);
        return toResponse(reservation);
    }

    private ReservationPendingResponse toResponse(ReservationPending pending) {
        ReservationPendingResponse response = new ReservationPendingResponse();
        response.setId(pending.getId() != null ? pending.getId().longValue() : null);
        response.setActivityId(pending.getActivityId() != null ? pending.getActivityId().longValue() : null);
        response.setCompanyId(pending.getCompanyId() != null ? pending.getCompanyId().longValue() : null);
        response.setActivityTitle(pending.getActivityTitle());
        response.setStartAt(combine(pending.getActivityDate(), pending.getStartTime()));
        response.setEndAt(combine(pending.getActivityDate(), pending.getEndTime()));
        response.setTouristName(pending.getTouristName());
        response.setTouristEmail(pending.getTouristEmail());
        response.setTouristNationality(pending.getTouristNationality());
        response.setStatus(normalizeStatus(pending.getStatus()));
        response.setSpecialRequest(pending.getSpecialRequest());
        response.setCreatedAt(pending.getCreatedAt());
        return response;
    }

    private LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            return null;
        }
        return LocalDateTime.of(date, time);
    }

    private CompanyUser requireUser(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required.");
        }
        CompanyUser user = companyUserMapper.findById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required.");
        }
        return user;
    }

    private boolean isAdmin(String role) {
        return role != null && ROLE_ADMIN.equalsIgnoreCase(role);
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return null;
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}
