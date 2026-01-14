package com.example.hangangactivity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.model.Reservation;
import com.example.hangangactivity.model.ReservationPending;

@Mapper
public interface ReservationMapper {

    List<ReservationPending> findReservationsForCompany(@Param("companyId") Long companyId);

    List<ReservationPending> findPendingByCompanyId(@Param("companyId") Long companyId);

    ReservationPending findPendingById(@Param("reservationId") Long reservationId);

    ReservationPending findById(@Param("reservationId") Long reservationId);

    List<ReservationPending> findByActivityIdAndStatus(@Param("activityId") Long activityId,
                                                       @Param("status") String status);

    int updateStatus(@Param("reservationId") Long reservationId,
                     @Param("status") String status);
    int insert(Reservation reservation);
}

