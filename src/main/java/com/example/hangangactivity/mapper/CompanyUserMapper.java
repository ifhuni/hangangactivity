package com.example.hangangactivity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.model.CompanyUser;

@Mapper
public interface CompanyUserMapper {

    CompanyUser findByUsername(@Param("username") String username);

    void insert(CompanyUser user);
}
