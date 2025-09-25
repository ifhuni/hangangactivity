package com.example.hangangactivity.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.ReservationCreateRequest;
import com.example.hangangactivity.dto.ReservationPendingResponse;
import com.example.hangangactivity.mapper.ActivityMapper;
import com.example.hangangactivity.mapper.CompanyUserMapper;
import com.example.hangangactivity.mapper.ReservationMapper;
import com.example.hangangactivity.mapper.TouristMapper;
import com.example.hangangactivity.model.Activity;
import com.example.hangangactivity.model.CompanyUser;
import com.example.hangangactivity.model.Reservation;
import com.example.hangangactivity.model.ReservationPending;
import com.example.hangangactivity.model.Tourist;

@Service
public class ReservationService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final String STATUS_PENDING = "pending";

    private final ReservationMapper reservationMapper;
    private final CompanyUserMapper companyUserMapper;
    private final ActivityMapper activityMapper;
    private final TouristMapper touristMapper;

    public ReservationService(ReservationMapper reservationMapper,
                              CompanyUserMapper companyUserMapper,
                              ActivityMapper activityMapper,
                              TouristMapper touristMapper) {
        this.reservationMapper = reservationMapper;
        this.companyUserMapper = companyUserMapper;
        this.activityMapper = activityMapper;
        this.touristMapper = touristMapper;
    }

    @Transactional
    public ReservationPendingResponse createReservation(ReservationCreateRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reservation data is required.");
        }

        Long activityId = request.getActivityId();
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected activity does not exist.");
        }

        int maxParticipants = activity.getMaxParticipants() != null ? activity.getMaxParticipants() : 0;
        int confirmedParticipants = activity.getCurrentParticipants() != null ? activity.getCurrentParticipants() : 0;
        int leftSeats = Math.max(0, maxParticipants - confirmedParticipants);

        int peopleCount = request.getPeopleCount() != null ? request.getPeopleCount() : 1;
        if (peopleCount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one participant is required.");
        }
        if (leftSeats < peopleCount) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Not enough seats left. Available: " + leftSeats);
        }

        Tourist tourist = findOrCreateTourist(request);
        if (tourist == null || tourist.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to prepare tourist information.");
        }

        Reservation reservation = new Reservation();
        reservation.setActivityId(activityId);
        reservation.setTouristId(tourist.getId());
        reservation.setStatus(STATUS_PENDING);
        reservation.setSpecialRequest(buildSpecialRequest(request, peopleCount));

        int inserted = reservationMapper.insert(reservation);
        if (inserted == 0 || reservation.getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save reservation.");
        }

        ReservationPending pending = reservationMapper.findPendingById(reservation.getId());
        if (pending == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load reservation data.");
        }
        return toResponse(pending);
    }

    private Tourist findOrCreateTourist(ReservationCreateRequest request) {
        String passportNumber = request.getPassportNumber();
        Tourist existing = touristMapper.findByPassportNumber(passportNumber);
        if (existing != null) {
            boolean requiresUpdate = false;
            if (StringUtils.hasText(request.getTouristName()) && !Objects.equals(existing.getName(), request.getTouristName())) {
                existing.setName(request.getTouristName());
                requiresUpdate = true;
            }
            if (StringUtils.hasText(request.getEmail()) && !Objects.equals(existing.getEmail(), request.getEmail())) {
                existing.setEmail(request.getEmail());
                requiresUpdate = true;
            }
            if (StringUtils.hasText(request.getNationality()) && !Objects.equals(existing.getNationality(), request.getNationality())) {
                existing.setNationality(request.getNationality());
                requiresUpdate = true;
            }
            if (request.getBirthdate() != null && !Objects.equals(existing.getBirthdate(), request.getBirthdate())) {
                existing.setBirthdate(request.getBirthdate());
                requiresUpdate = true;
            }
            String normalizedGender = normalizeGender(request.getGender());
            if (!Objects.equals(existing.getGender(), normalizedGender)) {
                existing.setGender(normalizedGender);
                requiresUpdate = true;
            }
            if (requiresUpdate) {
                touristMapper.updateBasicInfo(existing);
            }
            return existing;
        }

        Tourist tourist = new Tourist();
        tourist.setName(request.getTouristName());
        tourist.setPassportNumber(passportNumber);
        tourist.setNationality(request.getNationality());
        tourist.setGender(normalizeGender(request.getGender()));
        tourist.setBirthdate(request.getBirthdate());
        tourist.setEmail(request.getEmail());
        touristMapper.insert(tourist);
        return tourist;
    }

    private String buildSpecialRequest(ReservationCreateRequest request, int peopleCount) {
        StringBuilder builder = new StringBuilder();
        builder.append("Participants: ").append(peopleCount).append(" person");
        if (peopleCount > 1) {
            builder.append('s');
        }
        String special = request.getSpecialRequest();
        if (StringUtils.hasText(special)) {
            builder.append("\n").append(special.trim());
        }
        return builder.toString();
    }

    private String normalizeGender(String gender) {
        if (!StringUtils.hasText(gender)) {
            return null;
        }
        String value = gender.trim().toUpperCase(Locale.ROOT);
        return switch (value) {
            case "M", "MALE" -> "M";
            case "F", "FEMALE" -> "F";
            default -> "Other";
        };
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
