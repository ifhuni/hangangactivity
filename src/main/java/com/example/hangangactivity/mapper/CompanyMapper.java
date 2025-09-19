package com.example.hangangactivity.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.model.Company;

@Mapper
public interface CompanyMapper {

    void insert(Company company);

    Company findById(@Param("id") Long id);

    void updateVerification(@Param("id") Long id, @Param("verified") boolean verified);
}