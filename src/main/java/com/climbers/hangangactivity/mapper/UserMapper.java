package com.climbers.hangangactivity.mapper;

import com.climbers.hangangactivity.model.User;

public interface UserMapper {
    
    @Insert("INSERT INTO public.users (email, password, name, phone, role, company_id) " +
            "VALUES (#{email}, #{password}, #{name}, #{phone}, #{role}, #{companyId})")
    void insertUser(User user);
}
