package com.example.hangangactivity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hangangactivity.dto.ReservationCreateRequest;
import com.example.hangangactivity.dto.ReservationPendingResponse;
import com.example.hangangactivity.service.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public/reservations")
@Validated
public class ReservationPublicApiController {

    private final ReservationService reservationService;

    public ReservationPublicApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationPendingResponse> create(@Valid @RequestBody ReservationCreateRequest request) {
        ReservationPendingResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
