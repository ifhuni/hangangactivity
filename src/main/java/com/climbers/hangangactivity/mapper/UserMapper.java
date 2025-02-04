package com.climbers.hangangactivity.mapper;

import com.climbers.hangangactivity.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User findById(@Param("id") Integer id);
    List<User> findAll();
    void insert(User user);
    void update(User user);
    void delete(@Param("id") Integer id);
}
