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
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_UNASSIGNED = "UNASSIGNED";
    private static final String DEFAULT_STATUS = "DRAFT";

    private final ActivityMapper activityMapper;
    private final CompanyUserMapper companyUserMapper;
    private final FileStorageService fileStorageService;

    public ActivityService(ActivityMapper activityMapper, CompanyUserMapper companyUserMapper, FileStorageService fileStorageService) {
        this.activityMapper = activityMapper;
        this.companyUserMapper = companyUserMapper;
        this.fileStorageService = fileStorageService;
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
        boolean isAdmin = isAdminRole(requesterRole);

        Long companyId = request.getCompanyId();
        if (companyId == null) {
            companyId = user.getCompanyId();
        }

        String membershipStatus = validateCompanyAccess(user, isAdmin, companyId, true);

        Activity activity = new Activity();
        applyRequestToActivity(activity, null, request, companyId, membershipStatus, isAdmin);
        activityMapper.insert(activity);

        Activity inserted = activityMapper.findById(activity.getId().longValue());
        return toResponse(inserted);
    }

    @Transactional
    public ActivityResponse updateActivity(Long requesterUserId, String requesterRole, Long activityId, ActivityCreateRequest request) {
        CompanyUser user = requireUser(requesterUserId);
        boolean isAdmin = isAdminRole(requesterRole);

        Activity existing = requireActivity(activityId);
        Long companyId = existing.getCompanyId() != null ? existing.getCompanyId().longValue() : null;
        String membershipStatus = validateCompanyAccess(user, isAdmin, companyId, true);

        applyRequestToActivity(existing, existing, request, companyId, membershipStatus, isAdmin);
        existing.setId(activityId.intValue());
        int updated = activityMapper.update(existing);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Activity update failed.");
        }
        return toResponse(activityMapper.findById(activityId));
    }

    public ActivityResponse getActivity(Long requesterUserId, String requesterRole, Long activityId) {
        CompanyUser user = requireUser(requesterUserId);
        boolean isAdmin = isAdminRole(requesterRole);

        Activity activity = requireActivity(activityId);
        Long companyId = activity.getCompanyId() != null ? activity.getCompanyId().longValue() : null;
        validateCompanyAccess(user, isAdmin, companyId, false);
        return toResponse(activity);
    }

    private void applyRequestToActivity(Activity target,
                                        Activity existing,
                                        ActivityCreateRequest request,
                                        Long companyId,
                                        String membershipStatus,
                                        boolean isAdmin) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company id is required.");
        }

        String title = normalizedOrThrow(request.getTitle(), "Activity title is required.");
        String location = normalizedOrThrow(request.getLocation(), "Location is required.");

        Integer capacity = request.getCapacity();
        if (capacity != null && capacity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity must be greater than zero.");
        }
        Integer capacityValue = capacity != null ? capacity : existing != null ? existing.getMaxParticipants() : 0;

        LocalDateTime startAt = request.getStartAt();
        LocalDateTime endAt = request.getEndAt();
        if (startAt == null || endAt == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end date-time are required.");
        }
        if (endAt.isBefore(startAt)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date-time must be after start date-time.");
        }

        String category = normalizeOrNull(request.getCategory());
        if (!StringUtils.hasText(category) && existing != null && StringUtils.hasText(existing.getActivityType())) {
            category = existing.getActivityType();
        }
        if (!StringUtils.hasText(category)) {
            category = "GENERAL";
        }

        String status = normalizeOrNull(request.getStatus());
        if (!StringUtils.hasText(status) && existing != null && StringUtils.hasText(existing.getStatus())) {
            status = existing.getStatus();
        }
        if (!StringUtils.hasText(status)) {
            status = DEFAULT_STATUS;
        }

        boolean isPendingCompany = !isAdmin && STATUS_PENDING.equalsIgnoreCase(membershipStatus);
        if (isPendingCompany) {
            if (existing != null && StringUtils.hasText(existing.getStatus()) && !DEFAULT_STATUS.equalsIgnoreCase(existing.getStatus())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pending approval companies can only manage draft activities.");
            }
            status = DEFAULT_STATUS;
        }

        if (request.isRemoveImage()) {
            target.setImageUrl(null);
        } else if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = fileStorageService.store(request.getImage());
            target.setImageUrl(imageUrl);
        } else if (existing != null) {
            target.setImageUrl(existing.getImageUrl());
        }


        target.setCompanyId(Math.toIntExact(companyId));
        target.setTitle(title);
        target.setDescription(request.getDescription());
        target.setLocation(location);
        target.setActivityType(category);
        target.setMaxParticipants(capacityValue);
        target.setActivityDate(startAt.toLocalDate());
        target.setStartTime(startAt.toLocalTime());
        target.setEndTime(resolveEndTime(endAt, startAt));
        target.setStatus(status.toUpperCase(Locale.ROOT));
        target.setPrice(request.getPrice() != null ? request.getPrice() : existing != null ? existing.getPrice() : null);
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

    private Activity requireActivity(Long activityId) {
        if (activityId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity id is required.");
        }
        Activity activity = activityMapper.findById(activityId);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found.");
        }
        return activity;
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

    private String validateCompanyAccess(CompanyUser user, boolean isAdmin, Long companyId, boolean requireApproval) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company id is required.");
        }
        if (isAdmin) {
            return normalizeStatus(user.getMembershipStatus());
        }
        if (!Objects.equals(user.getCompanyId(), companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage activities for your company.");
        }
        String normalizedStatus = normalizeStatus(user.getMembershipStatus());
        if (STATUS_UNASSIGNED.equals(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company registration must be completed before managing activities.");
        }
        if (requireApproval && !(STATUS_APPROVED.equals(normalizedStatus) || STATUS_PENDING.equals(normalizedStatus))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company approval must be completed before managing activities.");
        }
        return normalizedStatus;
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

    private String normalizeStatus(String status) {
        return StringUtils.hasText(status) ? status.trim().toUpperCase(Locale.ROOT) : STATUS_UNASSIGNED;
    }

    private boolean isAdminRole(String role) {
        return role != null && ROLE_ADMIN.equalsIgnoreCase(role);
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
