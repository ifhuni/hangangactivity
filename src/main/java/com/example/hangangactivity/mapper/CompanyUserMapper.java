package com.example.hangangactivity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.hangangactivity.dto.PendingCompanyRequest;
import com.example.hangangactivity.model.CompanyUser;

@Mapper
public interface CompanyUserMapper {

    CompanyUser findByUsername(@Param("username") String username);

    CompanyUser findById(@Param("id") Long id);

    void insert(CompanyUser user);

    void updateMembershipOnRegister(@Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("status") String status);

    void updateMembershipStatus(@Param("userId") Long userId,
            @Param("status") String status);

    List<PendingCompanyRequest> findPendingRequests();
}