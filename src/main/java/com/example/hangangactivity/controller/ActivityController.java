package com.example.hangangactivity.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.hangangactivity.dto.ActivityCreateRequest;
import com.example.hangangactivity.dto.ActivityResponse;
import com.example.hangangactivity.service.ActivityService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/companies/{companyId}/activities")
    public ResponseEntity<List<ActivityResponse>> listByCompany(@PathVariable("companyId") Long companyId,
                                                                HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);
        Long sessionCompanyId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_ID);
        if (!isAdmin(role) && (sessionCompanyId == null || !sessionCompanyId.equals(companyId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your company activities.");
        }

        return ResponseEntity.ok(activityService.listActivitiesForCompany(companyId));
    }

    @GetMapping("/activities/mine")
    public ResponseEntity<List<ActivityResponse>> listMine(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        return ResponseEntity.ok(activityService.listActivitiesForUser(userId));
    }

    @GetMapping("/activities/{activityId}")
    public ResponseEntity<ActivityResponse> findOne(@PathVariable("activityId") Long activityId,
                                                     HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);
        ActivityResponse response = activityService.getActivity(userId, role, activityId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/activities", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityResponse> createActivity(@RequestParam("title") String title,
                                                           @RequestParam(value = "category", required = false) String category,
                                                           @RequestParam(value = "description", required = false) String description,
                                                           @RequestParam("location") String location,
                                                           @RequestParam(value = "capacity", required = false) String capacity,
                                                           @RequestParam("startAt") String startAt,
                                                           @RequestParam("endAt") String endAt,
                                                           @RequestParam(value = "price", required = false) String price,
                                                           @RequestParam(value = "status", required = false) String status,
                                                           @RequestParam(value = "companyId", required = false) String companyIdParam,
                                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                                           HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        ActivityCreateRequest createRequest = buildRequest(title, category, description, location,
                capacity, startAt, endAt, price, status, companyIdParam, image, false);

        ActivityResponse response = activityService.createActivity(userId, role, createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/activities/{activityId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityResponse> updateActivity(@PathVariable("activityId") Long activityId,
                                                           @RequestParam("title") String title,
                                                           @RequestParam(value = "category", required = false) String category,
                                                           @RequestParam(value = "description", required = false) String description,
                                                           @RequestParam("location") String location,
                                                           @RequestParam(value = "capacity", required = false) String capacity,
                                                           @RequestParam("startAt") String startAt,
                                                           @RequestParam("endAt") String endAt,
                                                           @RequestParam(value = "price", required = false) String price,
                                                           @RequestParam(value = "status", required = false) String status,
                                                           @RequestParam(value = "companyId", required = false) String companyIdParam,
                                                           @RequestParam(value = "image", required = false) MultipartFile image,
                                                           @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
                                                           HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        ensureAuthenticated(session);

        Long userId = (Long) session.getAttribute(AuthController.SESSION_COMPANY_USER_ID);
        String role = (String) session.getAttribute(AuthController.SESSION_COMPANY_ROLE);

        ActivityCreateRequest updateRequest = buildRequest(title, category, description, location,
                capacity, startAt, endAt, price, status, companyIdParam, image, removeImage);

        ActivityResponse response = activityService.updateActivity(userId, role, activityId, updateRequest);
        return ResponseEntity.ok(response);
    }

    private ActivityCreateRequest buildRequest(String title,
                                               String category,
                                               String description,
                                               String location,
                                               String capacity,
                                               String startAt,
                                               String endAt,
                                               String price,
                                               String status,
                                               String companyIdParam,
                                               MultipartFile image,
                                               boolean removeImage) {
        Long companyId = parseLong(companyIdParam, "companyId");
        Integer capacityValue = parseInteger(capacity, "capacity");
        Integer priceValue = parseInteger(price, "price");
        LocalDateTime startAtValue = parseDateTime(startAt, "startAt");
        LocalDateTime endAtValue = parseDateTime(endAt, "endAt");

        return new ActivityCreateRequest(
                companyId,
                title,
                category,
                description,
                location,
                capacityValue,
                startAtValue,
                endAtValue,
                priceValue,
                status,
                image,
                removeImage
        );
    }

    private void ensureAuthenticated(HttpSession session) {
        if (session == null || session.getAttribute(AuthController.SESSION_COMPANY_USER_ID) == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required.");
        }
    }

    private boolean isAdmin(String role) {
        return role != null && "ADMIN".equalsIgnoreCase(role);
    }

    private Integer parseInteger(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be numeric.");
        }
    }

    private Long parseLong(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be numeric.");
        }
    }

    private LocalDateTime parseDateTime(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be a valid ISO date-time (yyyy-MM-dd'T'HH:mm).");
        }
    }
}