package com.climbers.hangangactivity.mapper;

import com.climbers.hangangactivity.model.User;
import org.apache.ibatis.annotations.Mapper; // MyBatis의 @Mapper import

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User findByEmail(String email);
}
