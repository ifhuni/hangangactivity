package com.climbers.hangangactivity.mapper;

import com.climbers.hangangactivity.model.User;
import org.apache.ibatis.annotations.Mapper; // MyBatis의 @Mapper import
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    void insertUser(User user);

    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE email = #{email} AND password = #{password}")
    User findByEmailAndPassword(String email, String password);
}
