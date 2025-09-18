package com.example.hangangactivity.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hangangactivity.mapper.ActivityMapper;

@Controller
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ActivityMapper activityMapper;

    public ReservationController(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    @GetMapping("/reservation")
    public String reservationPage() {
        return "reservation"; // full page (if directly accessed)
    }

    @GetMapping("/fragments/reservation")
    public String reservationFragment(
            Model model,
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "dateStart", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @RequestParam(value = "dateEnd", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
            @RequestParam(value = "activityType", required = false) String activityType,
            @RequestParam(value = "peopleCount", required = false) Integer peopleCount
    ) {
        log.info("Filters - region={}, startDate={}, endDate={}, activityType={}, peopleCount={}",
                region, dateStart, dateEnd, activityType, peopleCount);
        model.addAttribute("activities", activityMapper.findByFilter(region, dateStart, dateEnd, activityType, peopleCount));
        return "fragments/reservation :: reservation";
    }
}
