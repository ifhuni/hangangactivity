package com.example.hangangactivity.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.model.Activity;

@Mapper
public interface ActivityMapper {
    List<Activity> findAll();

    List<Activity> findByRegion(@Param("region") String region);

    List<Activity> findByFilter(
            @Param("region") String region,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("activityType") String activityType,
            @Param("peopleCount") Integer peopleCount);

    List<Activity> findByCompanyId(@Param("companyId") Long companyId);

    Activity findById(@Param("id") Long id);

    void insert(Activity activity);
}
