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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.ActivityCreateRequest;
import com.example.hangangactivity.dto.ActivityResponse;
import com.example.hangangactivity.mapper.ActivityMapper;
import com.example.hangangactivity.mapper.CompanyUserMapper;
import com.example.hangangactivity.model.Activity;
import com.example.hangangactivity.model.CompanyUser;

@Service
public class ActivityService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String DEFAULT_STATUS = "DRAFT";

    private final ActivityMapper activityMapper;
    private final CompanyUserMapper companyUserMapper;

    public ActivityService(ActivityMapper activityMapper, CompanyUserMapper companyUserMapper) {
        this.activityMapper = activityMapper;
        this.companyUserMapper = companyUserMapper;
    }

    public List<ActivityResponse> listActivitiesForCompany(Long companyId) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company id is required.");
        }
        return activityMapper.findByCompanyId(companyId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ActivityResponse> listActivitiesForUser(Long userId) {
        CompanyUser user = requireUser(userId);
        if (user.getCompanyId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company registration is required.");
        }
        return listActivitiesForCompany(user.getCompanyId());
    }

    @Transactional
    public ActivityResponse createActivity(Long requesterUserId, String requesterRole, ActivityCreateRequest request) {
        CompanyUser user = requireUser(requesterUserId);
        boolean isAdmin = requesterRole != null && ROLE_ADMIN.equalsIgnoreCase(requesterRole);

        Long companyId = request.getCompanyId();
        if (companyId == null) {
            companyId = user.getCompanyId();
        }

        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company id is required.");
        }

        if (!isAdmin && !companyId.equals(user.getCompanyId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage activities for your company.");
        }

        if (!isAdmin) {
            String membership = user.getMembershipStatus();
            if (!STATUS_APPROVED.equalsIgnoreCase(membership)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company approval must be completed before creating activities.");
            }
        }

        String title = normalizedOrThrow(request.getTitle(), "Activity title is required.");
        String location = normalizedOrThrow(request.getLocation(), "Location is required.");

        Integer capacity = request.getCapacity();
        if (capacity != null && capacity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity must be greater than zero.");
        }

        LocalDateTime startAt = request.getStartAt();
        LocalDateTime endAt = request.getEndAt();
        if (startAt == null || endAt == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end date-time are required.");
        }
        if (endAt.isBefore(startAt)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date-time must be after start date-time.");
        }

        String category = normalizeOrNull(request.getCategory());
        if (!StringUtils.hasText(category)) {
            category = "GENERAL";
        }

        String status = normalizeOrNull(request.getStatus());
        if (!StringUtils.hasText(status)) {
            status = DEFAULT_STATUS;
        }

        Activity activity = new Activity();
        activity.setCompanyId(Math.toIntExact(companyId));
        activity.setTitle(title);
        activity.setDescription(request.getDescription());
        activity.setLocation(location);
        activity.setActivityType(category);
        activity.setMaxParticipants(capacity != null ? capacity : 0);
        activity.setActivityDate(startAt.toLocalDate());
        activity.setStartTime(startAt.toLocalTime());
        activity.setEndTime(resolveEndTime(endAt, startAt));
        activity.setStatus(status.toUpperCase(Locale.ROOT));
        activity.setPrice(request.getPrice());
        activity.setImageUrl(null);

        activityMapper.insert(activity);

        Activity inserted = activityMapper.findById(activity.getId().longValue());
        return toResponse(inserted);
    }

    private LocalTime resolveEndTime(LocalDateTime endAt, LocalDateTime startAt) {
        LocalDate endDate = endAt.toLocalDate();
        LocalDate startDate = startAt.toLocalDate();
        if (endDate.isBefore(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before the start date.");
        }
        if (!endDate.equals(startDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be on the same day as the start date.");
        }
        return endAt.toLocalTime();
    }

    private ActivityResponse toResponse(Activity activity) {
        if (activity == null) {
            return null;
        }
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId() != null ? activity.getId().longValue() : null);
        response.setCompanyId(activity.getCompanyId() != null ? activity.getCompanyId().longValue() : null);
        response.setTitle(activity.getTitle());
        response.setDescription(activity.getDescription());
        response.setCategory(activity.getActivityType());
        response.setLocation(activity.getLocation());
        response.setCapacity(activity.getMaxParticipants());
        response.setCurrentParticipants(activity.getCurrentParticipants());
        response.setPrice(activity.getPrice());
        response.setStatus(activity.getStatus());
        response.setImageUrl(activity.getImageUrl());
        response.setStartAt(combine(activity.getActivityDate(), activity.getStartTime()));
        response.setEndAt(combine(activity.getActivityDate(), activity.getEndTime()));
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

    private String normalizedOrThrow(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return value.trim();
    }

    private String normalizeOrNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}


