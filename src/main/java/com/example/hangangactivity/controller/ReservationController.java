package com.example.hangangactivity.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hangangactivity.mapper.ActivityMapper;

@Controller
public class ReservationController {

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
        System.out.println("Filters - Region: " + region + ", Start Date: " + dateStart + ", End Date: " + dateEnd + ", Activity Type: " + activityType + ", People: " + peopleCount);
        model.addAttribute("activities", activityMapper.findByFilter(region, dateStart, dateEnd, activityType, peopleCount));
        return "fragments/reservation :: reservation";
    }
}
