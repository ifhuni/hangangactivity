package com.climbers.hangangactivity.service;

import com.climbers.hangangactivity.mapper.UserMapper;
import com.climbers.hangangactivity.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(User user) {
        // 이메일 중복 확인
        if (userMapper.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
    }
}
