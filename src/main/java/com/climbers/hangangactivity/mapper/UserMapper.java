package com.climbers.hangangactivity.mapper;

import com.climbers.hangangactivity.model.User;

public interface UserMapper {
    void insertUser(User user);
    User findByEmail(String email);
}
