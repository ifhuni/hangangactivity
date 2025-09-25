package com.example.hangangactivity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.model.Tourist;

@Mapper
public interface TouristMapper {

    Tourist findByPassportNumber(@Param("passportNumber") String passportNumber);

    int insert(Tourist tourist);

    int updateBasicInfo(Tourist tourist);
}
