package com.example.hangangactivity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.model.Reservation;
import com.example.hangangactivity.model.ReservationPending;

@Mapper
public interface ReservationMapper {

    List<ReservationPending> findAllPending();

    List<ReservationPending> findPendingByCompanyId(@Param("companyId") Long companyId);

    ReservationPending findPendingById(@Param("reservationId") Long reservationId);

    int updateStatus(@Param("reservationId") Long reservationId,
                     @Param("status") String status);

    int insert(Reservation reservation);
}
